import {
  type Message,
  convertToCoreMessages,
  createDataStreamResponse,
  smoothStream,
  streamText,
} from 'ai';

import { auth } from '@/app/(auth)/auth';
import { customModel } from '@/lib/ai';
import { models } from '@/lib/ai/models';
import { systemPrompt } from '@/lib/ai/prompts';
import {
  deleteChatById,
  getChatById,
  saveChat,
  saveMessages,
} from '@/lib/db/queries';
import {
  generateUUID,
  getMostRecentUserMessage,
  sanitizeResponseMessages,
} from '@/lib/utils';

import { generateTitleFromUserMessage } from '../../actions';
import { createDocument } from '@/lib/ai/tools/create-document';
import { updateDocument } from '@/lib/ai/tools/update-document';
import { requestSuggestions } from '@/lib/ai/tools/request-suggestions';
import { getWeather } from '@/lib/ai/tools/get-weather';
import { searchAnserini, formatSearchResultsToMarkdown } from '@/lib/ai/services/anserini';

export const maxDuration = 60;

type AllowedTools =
  | 'createDocument'
  | 'updateDocument'
  | 'requestSuggestions'
  | 'getWeather';

const blocksTools: AllowedTools[] = [
  'createDocument',
  'updateDocument',
  'requestSuggestions',
];

const weatherTools: AllowedTools[] = ['getWeather'];
const allTools: AllowedTools[] = [...blocksTools, ...weatherTools];

export async function POST(request: Request) {
  const {
    id,
    messages,
    modelId,
  }: { id: string; messages: Array<Message>; modelId: string } =
    await request.json();

  // TODO: REMOVE
  // Log the incoming request
  console.log('⭐ Chat API received request:', { id, modelId });

  const session = await auth();

  if (!session || !session.user || !session.user.id) {
    return new Response('Unauthorized', { status: 401 });
  }

  const model = models.find((model) => model.id === modelId);

  // TODO: REMOVE
  // Log the found model
  console.log('🌟 Using model:', model);

  if (!model) {
    return new Response('Model not found', { status: 404 });
  }

  // Handle Anserini search
  if (model.type === 'search') {
    const coreMessages = convertToCoreMessages(messages);
    const userMessage = getMostRecentUserMessage(coreMessages);
    if (!userMessage) {
      return new Response('No user message found', { status: 400 });
    }

    console.log('🌟 User message:', userMessage);

    // Save the chat if it doesn't exist
    const chat = await getChatById({ id });
    if (!chat) {
      const title = await generateTitleFromUserMessage({ message: userMessage });
      await saveChat({ id, userId: session.user.id, title });
    }

    // Generate message IDs
    const userMessageId = generateUUID();
    const assistantMessageId = generateUUID();

    // Save the user message
    await saveMessages({
      messages: [
        { ...userMessage, id: userMessageId, createdAt: new Date(), chatId: id },
      ],
    });

    try {
      if (!model.indexId) {
        throw new Error('Index ID is required for search models');
      }
      // Search using Anserini service
      const searchResults = await searchAnserini({
        query: userMessage.content.toString(),
        indexId: model.indexId,
      });

      // Format results as markdown
      const formattedResults = formatSearchResultsToMarkdown(searchResults, model.indexId);

      // Save the assistant message
      await saveMessages({
        messages: [
          {
            id: assistantMessageId,
            role: 'assistant',
            content: formattedResults,
            createdAt: new Date(),
            chatId: id
          }
        ]
      });

    console.log("messages:", JSON.stringify(messages, null, 2))
      // Return as a streaming response
      return createDataStreamResponse({
        execute: async (dataStream) => {
          // Write the user message ID first
          dataStream.writeData({
            type: 'user-message-id',
            content: userMessageId,
          });

          // Create a result object similar to what streamText returns
          const result = {
            fullStream: (async function* () {
              yield {
                type: 'text-delta',
                textDelta: formattedResults
              };
            })(),
            mergeIntoDataStream: async (ds: typeof dataStream) => {
              // Write the text content
              ds.writeData({
                type: 'text-delta',
                content: formattedResults
              });
              
              // Write the assistant message ID
              ds.writeMessageAnnotation({
                messageIdFromServer: assistantMessageId,
              });

              // Signal completion
              ds.writeData({ type: 'finish', content: '' });
            }
          };

        console.log("result:", JSON.stringify(result, null, 2))

          // Use the same pattern as the OpenAI flow
          await result.mergeIntoDataStream(dataStream);
        }
      });
    } catch (error) {
      console.error('Error processing Anserini search:', error);
      return new Response('Error processing search request', { status: 500 });
    }
  }

  const coreMessages = convertToCoreMessages(messages);
  const userMessage = getMostRecentUserMessage(coreMessages);

  if (!userMessage) {
    return new Response('No user message found', { status: 400 });
  }

  const chat = await getChatById({ id });

  if (!chat) {
    const title = await generateTitleFromUserMessage({ message: userMessage });
    console.log("title:", title)
    await saveChat({ id, userId: session.user.id, title });
  }

  const userMessageId = generateUUID();

  await saveMessages({
    messages: [
      { ...userMessage, id: userMessageId, createdAt: new Date(), chatId: id },
    ],
  });
  console.log("messages:", JSON.stringify(messages, null, 2))

  return createDataStreamResponse({
    execute: (dataStream) => {
      dataStream.writeData({
        type: 'user-message-id',
        content: userMessageId,
      });

      const result = streamText({
        model: customModel(model.apiIdentifier),
        system: systemPrompt,
        messages: coreMessages,
        maxSteps: 5,
        experimental_activeTools: allTools,
        experimental_transform: smoothStream({ chunking: 'word' }),
        tools: {
          getWeather,
          createDocument: createDocument({ session, dataStream, model }),
          updateDocument: updateDocument({ session, dataStream, model }),
          requestSuggestions: requestSuggestions({
            session,
            dataStream,
            model,
          }),
        },
        onFinish: async ({ response }) => {
          if (session.user?.id) {
            try {
              const responseMessagesWithoutIncompleteToolCalls =
                sanitizeResponseMessages(response.messages);

              await saveMessages({
                messages: responseMessagesWithoutIncompleteToolCalls.map(
                  (message) => {
                    const messageId = generateUUID();

                    if (message.role === 'assistant') {
                      dataStream.writeMessageAnnotation({
                        messageIdFromServer: messageId,
                      });
                    }

                    return {
                      id: messageId,
                      chatId: id,
                      role: message.role,
                      content: message.content,
                      createdAt: new Date(),
                    };
                  },
                ),
              });
            } catch (error) {
              console.error('Failed to save chat');
            }
          }
        },
        experimental_telemetry: {
          isEnabled: true,
          functionId: 'stream-text',
        },
      });

      result.mergeIntoDataStream(dataStream);
    },
  });
}

export async function DELETE(request: Request) {
  const { searchParams } = new URL(request.url);
  const id = searchParams.get('id');

  if (!id) {
    return new Response('Not Found', { status: 404 });
  }

  const session = await auth();

  if (!session || !session.user) {
    return new Response('Unauthorized', { status: 401 });
  }

  try {
    const chat = await getChatById({ id });

    if (chat.userId !== session.user.id) {
      return new Response('Unauthorized', { status: 401 });
    }

    await deleteChatById({ id });

    return new Response('Chat deleted', { status: 200 });
  } catch (error) {
    return new Response('An error occurred while processing your request', {
      status: 500,
    });
  }
}

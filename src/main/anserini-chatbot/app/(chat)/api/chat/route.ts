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
      console.log('Processing Anserini search request');
      
      if (!model.indexId) {
        throw new Error('Index ID is required for search models');
      }
      
      const searchResults = await searchAnserini({
        query: userMessage.content.toString(),
        indexId: model.indexId,
      });

      console.log('Search results received:', searchResults);

      const formattedResults = formatSearchResultsToMarkdown(searchResults, model.indexId);
      
      // Save the assistant message
      const assistantMessage = {
        id: assistantMessageId,
        role: 'assistant',
        content: formattedResults,
        createdAt: new Date(),
        chatId: id
      };

      console.log('Saving assistant message:', assistantMessage);
      
      await saveMessages({
        messages: [assistantMessage]
      });

      // Check if the request accepts streaming
      const acceptsStreaming = request.headers.get('accept')?.includes('text/event-stream');

      if (!acceptsStreaming) {
        // Return direct JSON response
        return new Response(JSON.stringify({
          content: formattedResults,
          messageId: assistantMessageId,
        }), {
          headers: { 'Content-Type': 'application/json' },
        });
      }

      // Return streaming response for OpenAI compatibility
      return createDataStreamResponse({
        execute: async (dataStream) => {
          console.log('Starting stream response');
          
          // Write user message ID immediately
          dataStream.writeData({
            type: 'user-message-id',
            content: userMessageId,
          });

          // Write assistant message ID immediately
          dataStream.writeMessageAnnotation({
            messageIdFromServer: assistantMessageId,
          });

          // Stream the response in very small chunks for better UI responsiveness
          const chunks = formattedResults.match(/.{1,50}/g) || [];
          for (const chunk of chunks) {
            dataStream.writeData({
              type: 'text-delta',
              content: chunk
            });
            // Smaller delay for faster updates
            await new Promise(resolve => setTimeout(resolve, 5));
          }

          // Signal completion
          dataStream.writeData({ type: 'finish', content: '' });
          
          console.log('Stream response completed');
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

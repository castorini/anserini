'use client';

import type { Attachment, Message, CreateMessage, ChatRequestOptions } from 'ai';
import { useChat } from 'ai/react';
import { useState, useMemo } from 'react';
import useSWR, { useSWRConfig } from 'swr';


import { ChatHeader } from '@/components/chat-header';
import type { Vote } from '@/lib/db/schema';
import { fetcher } from '@/lib/utils';

import { Block } from './block';
import { MultimodalInput } from './multimodal-input';
import { Messages } from './messages';
import type { VisibilityType } from './visibility-selector';
import { useBlockSelector } from '@/hooks/use-block';

export function Chat({
  id,
  initialMessages,
  selectedModelId,
  selectedVisibilityType,
  isReadonly,
}: {
  id: string;
  initialMessages: Array<Message>;
  selectedModelId: string;
  selectedVisibilityType: VisibilityType;
  isReadonly: boolean;
}) {
  // Split into two separate components based on model type
  if (selectedModelId.includes('anserini')) {
    return (
      <SearchChat
        id={id}
        initialMessages={initialMessages}
        selectedModelId={selectedModelId}
        selectedVisibilityType={selectedVisibilityType}
        isReadonly={isReadonly}
      />
    );
  }

  return (
    <OpenAIChat
      id={id}
      initialMessages={initialMessages}
      selectedModelId={selectedModelId}
      selectedVisibilityType={selectedVisibilityType}
      isReadonly={isReadonly}
    />
  );
}

// Separate component for search functionality
function SearchChat({ id, initialMessages, selectedModelId, selectedVisibilityType, isReadonly }: {
  id: string;
  initialMessages: Array<Message>;
  selectedModelId: string;
  selectedVisibilityType: VisibilityType;
  isReadonly: boolean;
}) {
  const { mutate } = useSWRConfig();
  const [messages, setMessages] = useState<Array<Message>>(initialMessages);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [attachments, setAttachments] = useState<Array<Attachment>>([]);
  const isBlockVisible = useBlockSelector((state) => state.isVisible);

  // Split into two functions: one for the actual submission and one for the form handler
  const submitMessage = async () => {
    if (!input.trim()) return;
    
    setIsLoading(true);
    try {
      const response = await fetch('/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          id,
          messages: [...messages, { role: 'user', content: input }],
          modelId: selectedModelId,
        }),
      });

      const data = await response.json();
      
      setMessages([
        ...messages,
        { id: crypto.randomUUID(), role: 'user', content: input },
        { id: data.messageId, role: 'assistant', content: data.content }
      ]);
      
      setInput('');
      await mutate('/api/history');
    } finally {
      setIsLoading(false);
    }
  };

  // Form submit handler that matches MultimodalInput's expected type
  const handleSubmit = async (
    event?: { preventDefault?: () => void } | undefined,
    chatRequestOptions?: ChatRequestOptions
  ) => {
    event?.preventDefault?.();
    await submitMessage();
  };

  const append = async (
    message: Message | CreateMessage,
    chatRequestOptions?: ChatRequestOptions
  ): Promise<string | null | undefined> => {
    return null;
  };

  const reload = async (chatRequestOptions?: ChatRequestOptions): Promise<string | null | undefined> => {
    return null;
  };

  const { data: votes } = useSWR<Array<Vote>>(`/api/vote?chatId=${id}`, fetcher);

  return (
    <>
      <div key={`${id}-${selectedModelId}`} className="flex flex-col min-w-0 h-dvh bg-background">
        <ChatHeader
          chatId={id}
          selectedModelId={selectedModelId}
          selectedVisibilityType={selectedVisibilityType}
          isReadonly={isReadonly}
        />

        <Messages
          chatId={id}
          isLoading={isLoading}
          votes={votes}
          messages={messages}
          setMessages={setMessages}
          reload={reload}
          isReadonly={isReadonly}
          isBlockVisible={isBlockVisible}
        />

        <form onSubmit={handleSubmit} className="flex mx-auto px-4 bg-background pb-4 md:pb-6 gap-2 w-full md:max-w-3xl">
          {!isReadonly && (
            <MultimodalInput
              chatId={id}
              input={input}
              setInput={setInput}
              handleSubmit={handleSubmit}
              isLoading={isLoading}
              stop={() => {}}
              attachments={attachments}
              setAttachments={setAttachments}
              messages={messages}
              setMessages={setMessages}
              append={append}
            />
          )}
        </form>
      </div>

      <Block
        chatId={id}
        input={input}
        setInput={setInput}
        handleSubmit={handleSubmit}
        isLoading={isLoading}
        stop={() => {}}
        attachments={attachments}
        setAttachments={setAttachments}
        append={append}
        messages={messages}
        setMessages={setMessages}
        reload={reload}
        votes={votes}
        isReadonly={isReadonly}
      />
    </>
  );
}

// Original OpenAI chat functionality
function OpenAIChat({ id, initialMessages, selectedModelId, selectedVisibilityType, isReadonly }: {
  id: string;
  initialMessages: Array<Message>;
  selectedModelId: string;
  selectedVisibilityType: VisibilityType;
  isReadonly: boolean;
}) {
  const { mutate } = useSWRConfig();
  const [attachments, setAttachments] = useState<Array<Attachment>>([]);
  const isBlockVisible = useBlockSelector((state) => state.isVisible);

  const {
    messages,
    setMessages,
    handleSubmit,
    input,
    setInput,
    append,
    isLoading,
    stop,
    reload,
  } = useChat({
    id,
    body: { id, modelId: selectedModelId },
    initialMessages,
    experimental_throttle: 100,
    onFinish: () => {
      mutate('/api/history');
    },
  });

  const { data: votes } = useSWR<Array<Vote>>(`/api/vote?chatId=${id}`, fetcher);

  return (
    <>
      <div key={`${id}-${selectedModelId}`} className="flex flex-col min-w-0 h-dvh bg-background">
        <ChatHeader
          chatId={id}
          selectedModelId={selectedModelId}
          selectedVisibilityType={selectedVisibilityType}
          isReadonly={isReadonly}
        />

        <Messages
          chatId={id}
          isLoading={isLoading}
          votes={votes}
          messages={messages}
          setMessages={setMessages}
          reload={reload}
          isReadonly={isReadonly}
          isBlockVisible={isBlockVisible}
        />

        <form onSubmit={handleSubmit} className="flex mx-auto px-4 bg-background pb-4 md:pb-6 gap-2 w-full md:max-w-3xl">
          {!isReadonly && (
            <MultimodalInput
              chatId={id}
              input={input}
              setInput={setInput}
              handleSubmit={handleSubmit}
              isLoading={isLoading}
              stop={stop}
              attachments={attachments}
              setAttachments={setAttachments}
              messages={messages}
              setMessages={setMessages}
              append={append}
            />
          )}
        </form>
      </div>

      <Block
        chatId={id}
        input={input}
        setInput={setInput}
        handleSubmit={handleSubmit}
        isLoading={isLoading}
        stop={stop}
        attachments={attachments}
        setAttachments={setAttachments}
        append={append}
        messages={messages}
        setMessages={setMessages}
        reload={reload}
        votes={votes}
        isReadonly={isReadonly}
      />
    </>
  );
}

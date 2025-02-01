'use client';

import type { Attachment, Message } from 'ai';
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
  const { mutate } = useSWRConfig();
  const [localMessages, setLocalMessages] = useState<Message[]>(initialMessages);
  const isSearchModel = selectedModelId.includes('anserini');

  const {
    messages: aiMessages,
    setMessages,
    handleSubmit: originalHandleSubmit,
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
    experimental_throttle: isSearchModel ? 0 : 100,
  });

  // Custom submit handler for search models
  const handleSubmit = async (e?: React.FormEvent<HTMLFormElement>) => {
    // Prevent form submission if event exists
    if (e) {
      e.preventDefault();
    }
    
    if (!input.trim()) return;

    if (isSearchModel) {
      // Immediately append user message
      const userMessage: Message = {
        id: crypto.randomUUID(),
        role: 'user',
        content: input,
        createdAt: new Date(),
      };
      setLocalMessages(prev => [...prev, userMessage]);
      setInput('');

      try {
        // Direct API call for search
        const response = await fetch('/api/chat', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            id,
            messages: [...localMessages, userMessage],
            modelId: selectedModelId,
          }),
        });

        const data = await response.json();
        
        // Immediately append assistant message
        setLocalMessages(prev => [...prev, {
          id: crypto.randomUUID(),
          role: 'assistant',
          content: data.content,
          createdAt: new Date(),
        }]);

        await mutate('/api/history');
      } catch (error) {
        console.error('Search error:', error);
      }
    } else {
      // Use original handler for chat models
      if (e) {
        originalHandleSubmit(e);
      }
    }
  };

  // Use local messages for search model, AI messages for chat model
  const displayMessages = isSearchModel ? localMessages : aiMessages;

  const { data: votes } = useSWR<Array<Vote>>(
    `/api/vote?chatId=${id}`,
    fetcher,
  );

  const [attachments, setAttachments] = useState<Array<Attachment>>([]);
  const isBlockVisible = useBlockSelector((state) => state.isVisible);

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
          messages={displayMessages}
          setMessages={setMessages}
          reload={reload}
          isReadonly={isReadonly}
          isBlockVisible={isBlockVisible}
        />

        <form className="flex mx-auto px-4 bg-background pb-4 md:pb-6 gap-2 w-full md:max-w-3xl">
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
              messages={displayMessages}
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
        messages={displayMessages}
        setMessages={setMessages}
        reload={reload}
        votes={votes}
        isReadonly={isReadonly}
      />
    </>
  );
}

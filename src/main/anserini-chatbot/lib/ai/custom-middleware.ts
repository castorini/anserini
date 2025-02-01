import type { Message } from 'ai';


// None of these appear to be going through here for now

export const customMiddleware = {
  async transformOpenAIRequest(request: { messages: Message[]; model: string }) {
    // Log at entry point
    console.log('🟡 Middleware entered');
    
    // Log the raw request
    console.log('🔵 Raw request:', request);
    
    // Log all intercepted calls to debug
    console.log('🟢 Intercepted API call:', {
      messages: request.messages,
      model: request.model,
      apiIdentifier: request.model // This is what we set in models.ts
    });
    
    // Only intercept calls to our Anserini model
    if (request.model === 'anserini-java') {
      console.log('🔴 Intercepted Anserini API call');
    }
    
    return request;
  }
};

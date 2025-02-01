# AI-Powered Search Interface

A modern web application that provides a chat-like interface for interacting with various search indexes through Anserini's search API, built on top of Vercel's ([AI Chatbot Template](https://github.com/vercel/ai-chatbot)).

## Features

### Search Models
- **MS MARCO Collection**
  - Base Indexes (V1/V2)
  - Neural Encodings:
    - SPLADE++ CoCondenser-EnsembleDistil
    - cos-DPR Distil (HNSW & quantized INT8)
    - BGE-base-en-v1.5 (HNSW & quantized INT8)
    - Cohere embed-english-v3.0 (HNSW & quantized INT8)
- **BEIR Collections**
  - Multiple collections (TREC-COVID, BioASQ, etc.)
  - Index types: Flat, Multifield, SPLADE++, BGE
- **CACM Collection**
  - Classic IR test collection

### Chat Interface
- Conversational interaction with search results
- Real-time streaming responses
- Support for both search and chat models
- Message history and chat persistence

## Architecture

### Core Components
- Next.js 13+ with App Router
- TypeScript for type safety
- Vercel AI SDK for streaming
- Server-side API routes for search functionality

### Search Integration
- Direct integration with Anserini's Java backend APIs
- Support for multiple index types:
  - Traditional Lucene indexes
  - Neural HNSW indexes
  - Quantized (INT8) indexes
  - Impact indexes (SPLADE++)

### Data Flow
1. User sends query through chat interface
2. Query is processed by appropriate model
3. For search models:
   - Query is sent to Anserini backend
   - Results are formatted and streamed back
4. For chat models:
   - Interaction is handled by chat API
   - Responses are streamed in real-time

## Running locally

You will need to use the environment variables [defined in `.env.example`](.env.example) to run the application. It's recommended you use [Vercel Environment Variables](https://vercel.com/docs/projects/environment-variables) for this, but a `.env` file is all that is necessary.

> Note: You should not commit your `.env` file or it will expose secrets that will allow others to control access to your various OpenAI and authentication provider accounts.

1. Install Vercel CLI: `npm i -g vercel`
2. Link local instance with Vercel and GitHub accounts (creates `.vercel` directory): `vercel link`
3. Download your environment variables: `vercel env pull`

Then install dependencies and start the development server:

```bash
pnpm install
pnpm tsx lib/db/migrate.ts && pnpm run build
pnpm dev
```

Your app should now be running on [localhost:3000](http://localhost:3000/).

## Configuration

### Anserini Server
The application expects an Anserini server running at the configured host/port with the following settings:
```json
{
  "host": "localhost",
  "port": 8081,
  "apiVersion": "v1.0"
}
```

### Available Models
Models are configured in `lib/ai/models.ts` and include:
- Chat models for query assistance
- Search models for different collections and encodings

## Deploy Your Own

You can deploy your own version of the application to Vercel with one click:

[![Deploy with Vercel](https://vercel.com/button)](https://vercel.com/new/clone?repository-url=https%3A%2F%2Fgithub.com%2Fvercel%2Fai-chatbot&env=AUTH_SECRET,OPENAI_API_KEY&envDescription=Learn%20more%20about%20how%20to%20get%20the%20API%20Keys%20for%20the%20application&envLink=https%3A%2F%2Fgithub.com%2Fvercel%2Fai-chatbot%2Fblob%2Fmain%2F.env.example&demo-title=AI%20Chatbot&demo-description=An%20Open-Source%20AI%20Chatbot%20Template%20Built%20With%20Next.js%20and%20the%20AI%20SDK%20by%20Vercel.&demo-url=https%3A%2F%2Fchat.vercel.ai&stores=[{%22type%22:%22postgres%22},{%22type%22:%22blob%22}])

## API Integration

See `API_INTEGRATION.md` for detailed information about integrating with the Anserini backend and available endpoints.

## Development

The application uses a modern React stack with:
- Next.js App Router for routing
- Server Components for improved performance
- Streaming for real-time updates
- TypeScript for type safety
- [shadcn/ui](https://ui.shadcn.com) with Tailwind CSS
- [Vercel Postgres](https://vercel.com/storage/postgres) for data persistence
- [NextAuth.js](https://github.com/nextauthjs/next-auth) for authentication

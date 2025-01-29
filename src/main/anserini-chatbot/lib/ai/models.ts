// Define your models here.
export interface Model {
  id: string;
  label: string;
  apiIdentifier: string;
  description: string;
  type?: 'chat' | 'search';
  indexId?: string;
}

export const models: Array<Model> = [
// Chat models
  {
    id: 'gpt-4o-mini',
    label: 'GPT 4o mini',
    apiIdentifier: 'gpt-4o-mini',
    description: 'Ask me a question about your queries!',
    type: 'chat'
  },
  {
    id: 'gpt-4o',
    label: 'GPT 4o',
    apiIdentifier: 'gpt-4o',
    description: 'For complex questions about queries',
    type: 'chat'
  },

  // Search models
  {
    id: 'anserini-cacm',
    label: 'CACM',
    apiIdentifier: 'anserini-java',
    description: 'Lucene index of the CACM corpus',
    type: 'search',
    indexId: 'cacm'
  },
  {
    id: 'anserini-msmarco-base',
    label: 'MS MARCO V1 Base',
    apiIdentifier: 'anserini-java',
    description: 'Lucene index of the MS MARCO V1 passage corpus',
    type: 'search',
    indexId: 'msmarco-v1-passage'
  },
  {
    id: 'anserini-msmarco-splade',
    label: 'MS MARCO V1 SPLADE++',
    apiIdentifier: 'anserini-java',
    description: 'SPLADE++ CoCondenser-EnsembleDistil encoding',
    type: 'search',
    indexId: 'msmarco-v1-passage.splade-pp-ed'
  },
  {
    id: 'anserini-msmarco-cosdpr',
    label: 'MS MARCO V1 cos-DPR HNSW',
    apiIdentifier: 'anserini-java',
    description: 'cos-DPR Distil encoding with HNSW',
    type: 'search',
    indexId: 'msmarco-v1-passage.cosdpr-distil.hnsw'
  },
  {
    id: 'anserini-msmarco-cosdpr-int8',
    label: 'MS MARCO V1 cos-DPR HNSW INT8',
    apiIdentifier: 'anserini-java',
    description: 'cos-DPR Distil encoding with quantized HNSW',
    type: 'search',
    indexId: 'msmarco-v1-passage.cosdpr-distil.hnsw-int8'
  },
  {
    id: 'anserini-msmarco-bge',
    label: 'MS MARCO V1 BGE HNSW',
    apiIdentifier: 'anserini-java',
    description: 'BGE-base-en-v1.5 encoding with HNSW',
    type: 'search',
    indexId: 'msmarco-v1-passage.bge-base-en-v1.5.hnsw'
  },
  {
    id: 'anserini-msmarco-bge-int8',
    label: 'MS MARCO V1 BGE HNSW INT8',
    apiIdentifier: 'anserini-java',
    description: 'BGE-base-en-v1.5 encoding with quantized HNSW',
    type: 'search',
    indexId: 'msmarco-v1-passage.bge-base-en-v1.5.hnsw-int8'
  },
  {
    id: 'anserini-msmarco-cohere',
    label: 'MS MARCO V1 Cohere HNSW',
    apiIdentifier: 'anserini-java',
    description: 'Cohere embed-english-v3.0 encoding with HNSW',
    type: 'search',
    indexId: 'msmarco-v1-passage.cohere-embed-english-v3.0.hnsw'
  },
  {
    id: 'anserini-msmarco-cohere-int8',
    label: 'MS MARCO V1 Cohere HNSW INT8',
    apiIdentifier: 'anserini-java',
    description: 'Cohere embed-english-v3.0 encoding with quantized HNSW',
    type: 'search',
    indexId: 'msmarco-v1-passage.cohere-embed-english-v3.0.hnsw-int8'
  }
] as const;

export const DEFAULT_MODEL_NAME: string = 'gpt-4o-mini';
export const DEFAULT_SEARCH_MODEL: Model = models.find(m => m.id === 'anserini-msmarco-base') as Model;

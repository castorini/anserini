import anseriniConfig from '@/config/anserini.json';
import { models } from '@/lib/ai/models';

export interface SearchResult {
  docid: string;
  score: number;
  doc: {
    contents: string;
  };
}

export interface SearchResponse {
  candidates: SearchResult[];
}

export interface AnseriniSearchOptions {
  query: string;
  indexId?: string;
}

export const formatSearchResultsToMarkdown = (results: SearchResponse, indexId: string): string => {
  // Get the model info for this index
  const model = models.find(m => m.type === 'search' && m.indexId === indexId);
  const modelName = model ? model.label : indexId;
  const encoding = model ? model.description.split(' encoded by ')[1]?.replace('.', '') : '';
  const title = encoding ? `${modelName} (${encoding})` : modelName;
  
  return `### Search Results (${title})\n\n${results.candidates
    .map(
      (result: SearchResult, index: number) =>
        `${index + 1}. **DocID:** ${result.docid} | **Score:** ${result.score}\n\n${
          result.doc.contents
        }\n\n`
    )
    .join('')}`;
};

export const searchAnserini = async ({
  query,
  indexId,
}: AnseriniSearchOptions): Promise<SearchResponse> => {
  if (!indexId) {
    throw new Error('Index ID is required for Anserini search');
  }

  const baseUrl = `http://${anseriniConfig.host}:${anseriniConfig.port}/api/${anseriniConfig.apiVersion}`;
  const searchUrl = `${baseUrl}/indexes/${indexId}/search?query=${encodeURIComponent(
    query
  )}`;

  try {
    const response = await fetch(searchUrl);
    if (!response.ok) {
      throw new Error(`Anserini API returned ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error searching Anserini:', error);
    throw error;
  }
}; 

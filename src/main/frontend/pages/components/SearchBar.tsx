// components/SearchBar.tsx
import React, { useEffect, useState } from 'react';
import { QueryResult } from '../../types/QueryResult';

const SearchBar: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [results, setResults] = useState<QueryResult[]>([]);
  const [query, setQuery] = useState<string>('');
  
  const [group, setGroup] = useState<string>('msmarco-v1-passage');
  const [collection, setCollection] = useState<string>('');
  const [collections, setCollections] = useState<{[key: string]: string[]}>({
    'msmarco-v1-passage': [
      'msmarco-v1-passage',
      'msmarco-v1-passage.splade-pp-ed',
      'msmarco-v1-passage.cos-dpr-distil',
      'msmarco-v1-passage.cos-dpr-distil.quantized',
      'msmarco-v1-passage.bge-base-en-v1.5',
      'msmarco-v1-passage.bge-base-en-v1.5.quantized',
      'msmarco-v1-passage.cohere-embed-english-v3.0',
      'msmarco-v1-passage.cohere-embed-english-v3.0.quantized',
    ],
    'msmarco-v2-passage': [
      'msmarco-v2-passage',
    ],
    'msmarco-v2-doc': [
      'msmarco-v2-doc',
      'msmarco-v2-doc-segmented',
    ],
    'msmarco-v2.1-doc': [
      'msmarco-v2.1-doc',
      'msmarco-v2.1-doc-segmented',
    ],
  });

  // Generate collections for BEIR
  useEffect(() => {
    const keys = [
      'beir-v1.0.0-trec-covid',
      'beir-v1.0.0-bioasq',
      'beir-v1.0.0-nfcorpus',
      'beir-v1.0.0-nq',
      'beir-v1.0.0-hotpotqa',
      'beir-v1.0.0-fiqa',
      'beir-v1.0.0-signal1m',
      'beir-v1.0.0-trec-news',
      'beir-v1.0.0-robust04',
      'beir-v1.0.0-arguana',
      'beir-v1.0.0-webis-touche2020',
      'beir-v1.0.0-cqadupstack-android',
      'beir-v1.0.0-cqadupstack-english',
      'beir-v1.0.0-cqadupstack-gaming',
      'beir-v1.0.0-cqadupstack-gis',
      'beir-v1.0.0-cqadupstack-mathematica',
      'beir-v1.0.0-cqadupstack-physics',
      'beir-v1.0.0-cqadupstack-programmers',
      'beir-v1.0.0-cqadupstack-stats',
      'beir-v1.0.0-cqadupstack-tex',
      'beir-v1.0.0-cqadupstack-unix',
      'beir-v1.0.0-cqadupstack-webmasters',
      'beir-v1.0.0-cqadupstack-wordpress',
      'beir-v1.0.0-quora',
      'beir-v1.0.0-dbpedia-entity',
      'beir-v1.0.0-scidocs',
      'beir-v1.0.0-fever',
      'beir-v1.0.0-climate-fever',
      'beir-v1.0.0-scifact',
    ];
    const generatedMap: {[key: string]: string[]} = collections;
    keys.forEach(key => {
      generatedMap[key] = [
        `${key}.flat`,
        `${key}.multifield`,
        `${key}.splade-pp-ed`,
        `${key}.bge-base-en-v1.5`,
      ];
    });
    setCollections(generatedMap);
  }, []);

  const fetchResults = async (query: string, collection: string) => {
    setLoading(true);
    try {
      let endpoint = '/api';
      if (collection != '') endpoint += `/collection/${collection}`;
      endpoint += `/search?query=${query}`;
      
      const response = await fetch(endpoint);
      const data: QueryResult[] = await response.json();
      setResults(data);
    } catch (error) {
      console.error("Failed to fetch data: ", error);
      setResults([]);
    } finally {
        setLoading(false);
    }
  };

  const handleGroupChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setGroup(e.target.value);
  }

  const handleCollectionChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setCollection(e.target.value);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchResults(query, collection);
  };

  return (
    <>
    <div className="search-container">
      <form className="search-bar" onSubmit={handleSubmit}>
        <div className="dropdown-container">
          <select onChange={handleGroupChange} className="group-dropdown">
            {Object.keys(collections).map((group) => (
              <option key={group} value={group}>{group}</option>
            ))}
          </select>
          <select onChange={handleCollectionChange} className="collection-dropdown">
            {collections[group]?.map((collection) => (
              <option key={collection} value={collection}>{collection}</option>
            ))}
          </select>
        </div>
        <div className="searchbar-container">
          <input
            type="text"
            value={query}
            placeholder="Search..."
            className="search-input"
            onChange={(e) => setQuery(e.target.value)}
          />
          <button className="search-button" type="submit" disabled={loading}>Search</button>
        </div>
      </form>
    </div>
    {loading && <p>Loading...</p>}
    <ul>
      {results.map((result) => (
        <div className="query-card" key={result.docid}>
          <h3>Document ID: {result.docid} <span>Score: {result.score}</span></h3>
          <p>{result.content}</p>
        </div>
      ))}
    </ul>
    </>
  );
};

export default SearchBar;
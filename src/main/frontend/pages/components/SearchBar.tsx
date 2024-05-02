// components/SearchBar.tsx
import React, { useState } from 'react';
import { QueryResult } from '../../types/QueryResult';

const SearchBar: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [results, setResults] = useState<QueryResult[]>([]);
  const [query, setQuery] = useState<string>('');
  const [collection, setCollection] = useState<string>('');

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

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchResults(query, collection);
  };

  return (
    <>
    <div className="search-container">
      <form className="search-bar" onSubmit={handleSubmit}>
        <input
          type="text"
          value={query}
          placeholder="Search..."
          className="search-input"
          onChange={(e) => setQuery(e.target.value)}
        />
        <select
          value={collection}
          onChange={(e) => setCollection(e.target.value)}
          className="collection-dropdown"
        >
          <option value="msmarco-v1-passage" selected>msmarco-v1-passage</option>
          <option value="msmarco-v2-passage">msmarco-v2-passage</option>
          <option value="msmarco-v2.1-doc">msmarco-v2.1-doc</option>
          <option value="beir-v1.0.0-nfcorpus.flat">beir-v1.0.0-nfcorpus.flat</option>
        </select>
        <button className="search-button" type="submit" disabled={loading}>Search</button>
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
// components/SearchBar.tsx
import React, { useState } from 'react';
import { QueryResult } from '../../types/QueryResult';

const SearchBar: React.FC = () => {
  const [query, setQuery] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [results, setResults] = useState<QueryResult[]>([]);

  const fetchResults = async (query: string) => {
    setLoading(true);
    try {
      const response = await fetch(`/api/query/${query}`);
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
    fetchResults(query);
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
        <button className="search-button" type="submit" disabled={loading}>Search</button>
      </form>
    </div>
    {loading && <p>Loading...</p>}
    <ul>
      {results.map((result) => (
        <div className="query-card" key={result.docId}>
          <h3>Document ID: {result.docId} <span>Score: {result.score}</span></h3>
          <p>{result.content}</p>
        </div>
      ))}
    </ul>
    </>
  );
};

export default SearchBar;
// components/SearchBar.tsx
import React, { useState } from 'react';
import { QueryResult } from '../types/QueryResult';

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
    <div>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search..."
        />
        <button type="submit" disabled={loading}>
          Search
        </button>
      </form>
      {loading && <p>Loading...</p>}
      <ul>
        {results.map((result) => (
          <li key={result.docId}>{result.content} (Score: {result.score})</li>
        ))}
      </ul>
    </div>
  );
};

export default SearchBar;

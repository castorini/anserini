// components/SearchBar.tsx
import React, { useEffect, useState } from 'react';
import { QueryResult } from '../../types/QueryResult';
import Dropdown from './Dropdown';

const SearchBar: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [results, setResults] = useState<QueryResult[]>([]);
  const [query, setQuery] = useState<string>('');
  const [index, setIndex] = useState<string>('');

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
    fetchResults(query, index);
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
        <div className="search-buttons">
          <Dropdown onSelect={(selectedValue) => setIndex(selectedValue)} />
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
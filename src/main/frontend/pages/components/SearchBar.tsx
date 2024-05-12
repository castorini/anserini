/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        <Dropdown onSelect={(selectedValue) => setIndex(selectedValue)} />
        <div className="search-input-container">
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
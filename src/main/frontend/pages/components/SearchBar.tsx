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

import React, { useState } from 'react';
import Dropdown from './Dropdown';
import { Input, Button, Box, Spinner, Text, VStack, HStack } from '@chakra-ui/react';

const SearchBar: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [results, setResults] = useState<Array<any>>([]);
  const [query, setQuery] = useState<string>('');
  const [index, setIndex] = useState<string>('');

  const fetchResults = async (query: string, index: string) => {
    setLoading(true);
    try {
      let endpoint = '/api/v1.0';
      if (index !== '') endpoint += `/indexes/${index}`;
      endpoint += `/search?query=${query}`;
      
      const response = await fetch(endpoint);
      const data = await response.json();
      console.log(data);
      setResults(data.candidates);
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
      <Box className="search-container" p={4}>
        <form className="search-bar" onSubmit={handleSubmit}>
          <HStack spacing={4}>
            <Dropdown onSelect={(selectedValue) => setIndex(selectedValue)} />
            <Input
              type="text"
              value={query}
              placeholder="Search..."
              onChange={(e) => setQuery(e.target.value)}
            />
            <Button type="submit" isLoading={loading}>Search</Button>
          </HStack>
        </form>
      </Box>
      {loading && <Spinner />}
      <VStack spacing={4} align="stretch">
        {results.map((result, index) => (
          <Box key={index} p={4} shadow="md" borderWidth="1px">
            <Text as="h3">
              Document ID: {result.docid} <Text as="span">Score: {result.score}</Text>
            </Text>
            {Object.entries(result.doc).map(([key, value]) => (
              <Text key={key}>
                <strong>{key}:</strong> {JSON.stringify(value)}
              </Text>
            ))}
          </Box>
        ))}
      </VStack>
    </>
  );
};

export default SearchBar;
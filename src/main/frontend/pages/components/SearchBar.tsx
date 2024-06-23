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
import { Input, Button, Box, Spinner, Text, VStack, HStack, Container, Heading, Divider } from '@chakra-ui/react';

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
    <Container maxW="container.md" mt={8} p={4} boxShadow="lg" borderRadius="md" bg="white">
      <VStack spacing={6} align="stretch">
        <Heading as="h1" size="xl" textAlign="center">Anserini 2 Search</Heading>
        <Divider />
        <Box p={4} borderWidth="1px" borderRadius="md">
          <form onSubmit={handleSubmit}>
            <VStack spacing={4}>
              {/* all items left aligned */}
              <Box w="100%" p={4} borderWidth="1px" borderRadius="md" ml="auto">
                <Dropdown onSelect={(selectedValue) => setIndex(selectedValue)} />
              </Box>
              <HStack spacing={4}>
                <Input
                  type="text"
                  value={query}
                  placeholder="Type your query here..."
                  onChange={(e) => setQuery(e.target.value)}
                  bg="gray.100"
                  border="none"
                  _focus={{ bg: 'white', boxShadow: 'outline' }}
                />
                <Button type="submit" colorScheme="blue" isLoading={loading}>Search</Button>
              </HStack>
            </VStack>
          </form>
        </Box>
        {loading && <Spinner size="lg" />}
        <VStack spacing={4} align="stretch">
          {results.map((result, index) => (
            <Box key={index} p={4} shadow="md" borderWidth="1px" borderRadius="md">
              <Text as="h3" fontWeight="bold">
                Document ID: {result.docid} <Text as="span" fontWeight="normal">Score: {result.score}</Text>
              </Text>
              {Object.entries(result.doc).map(([key, value]) => (
                <Text key={key}>
                  <strong>{key}:</strong> {JSON.stringify(value)}
                </Text>
              ))}
            </Box>
          ))}
        </VStack>
      </VStack>
    </Container>
  );
};

export default SearchBar;

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
import { Input, Button, Box, Spinner, Text, VStack, HStack, Container, Heading, Divider, Flex, FormControl, Center, Select } from '@chakra-ui/react';

const SearchBar: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [results, setResults] = useState<Array<any>>([]);
  const [query, setQuery] = useState<string>('');
  const [queryType, setQueryType] = useState<string>('search query');
  const [index, setIndex] = useState<string>('');

  const fetchResults = async (query: string, index: string) => {
    setLoading(true);
    try {
      let endpoint = '/api/v1.0';
      if (index !== '') endpoint += `/indexes/${index}`;
      if (queryType === 'search query') {
        endpoint += `/search?query=${query}`;
        
        const response = await fetch(endpoint);
        const data = await response.json();
        console.log(data);
        setResults(data.candidates);
      } else {
        endpoint += `/documents/${query}`;
        const response = await fetch(endpoint);
        const data = await response.json();
        setResults([data]);
      }
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
    <Flex maxW="container.xl" height="90vh" mt="3vh" mx="auto" p={4} boxShadow="lg" borderRadius="lg" bg="white" direction="column" justifyContent="space-between">
      <Heading as="h1" size="xl" textAlign="center">Anserini Search</Heading>
      <Divider />
      <Box height="90%" p={4}>
        <form style={{ height: "100%" }} onSubmit={handleSubmit}>
          <Flex direction="column" gap={4} height="100%">
            <Dropdown onSelect={(selectedValue) => setIndex(selectedValue)} />
            <HStack spacing={4}>
              <Select
                defaultValue="search query"
                placeholder="Search by..."
                onChange={(e) => setQueryType(e.target.value)}
                width="150px"
              >
                <option value="search query">By query</option>
                <option value="docid query">By docid</option>
              </Select>
              <Input
                type="text"
                value={query}
                placeholder="Type your query here..."
                onChange={(e) => setQuery(e.target.value)}
                bg="gray.100"
                border="none"
                width="100%"
                _focus={{ bg: 'white', boxShadow: 'outline' }}
              />
              <Button type="submit" colorScheme="blue" isLoading={loading}>Go!</Button>
            </HStack>
            <Box p={4} borderWidth="1px" borderRadius="md" overflowY="auto" flexGrow={1}>
            {loading && <Spinner size="lg" />}
              <VStack height="100%" spacing={4} align="stretch">
                {results.map((result, index) => (
                  <Box key={index} p={4} shadow="md" borderWidth="1px" borderRadius="md">
                    <Flex justifyContent="space-between" alignItems="center" direction="row">
                      {result.docid && <Text as="h3" fontWeight="bold">
                        Document ID: {result.docid}
                      </Text>}
                      {result.score && <Text as="span" fontWeight="normal">Score: {result.score}</Text>}
                    </Flex>
                    {result.doc && Object.entries(result.doc).map(([key, value]) => (
                      <Text key={key}>
                        <strong>{key}:</strong> {JSON.stringify(value)}
                      </Text>
                    ))}
                  </Box>
                ))}
              </VStack>
            </Box>
          </Flex>
        </form>
      </Box>
    </Flex>
  );
};

export default SearchBar;

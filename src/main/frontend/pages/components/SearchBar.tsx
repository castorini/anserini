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
import { Input, Button, Box, Spinner, Text, VStack, HStack, Container, Heading, Divider, Flex, FormControl, Center, Select, Image } from '@chakra-ui/react';

const SearchBar: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [results, setResults] = useState<Array<any>>([]);
  const [query, setQuery] = useState<string>('');
  const [queryType, setQueryType] = useState<string>('search query');
  const [index, setIndex] = useState<string>('');
  const [searchPerformed, setSearchPerformed] = useState(false);

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
    setSearchPerformed(true);
  };

  return (
    <VStack direction="column" justifyContent="space-between">
       <form style={{ width: searchPerformed ? "100%" : "calc(100% - 400px)", position: "sticky", top: 0, 
                    zIndex: 10, backgroundColor: "white", padding: searchPerformed ? "16px" : "16px 80px", 
                    boxShadow: searchPerformed ? "0 4px 6px rgba(0, 0, 0, 0.1)" : "", height: "100%",
                    margin: searchPerformed ? "0" : "0 auto", transition: "padding 0.3s ease, box-shadow 0.3s ease"
                   }} onSubmit={handleSubmit}>
        <Flex direction={searchPerformed ? 'row' : 'column'} marginTop={searchPerformed ? '10px' : 'auto'} marginBottom={searchPerformed ? '0px' : 'auto'}>
          <Flex direction="column" style={{width: searchPerformed ? "80px" : "100%", marginRight: '20px' }}>
            <Image src="anserini-logo.png" alt="Anserini Logo" 
                style={{ width: searchPerformed ? "80px" : "180px", height: "auto", 
                        display: searchPerformed ? 'inline-block' : 'block', margin: '0 auto', marginBottom: '15px', marginTop: searchPerformed ? '0px' : '200px'}}  />
            <Heading as="h1" size={searchPerformed ? 's' : 'xl'} 
                  style={{ width: searchPerformed? '80px' : 'auto', textAlign: 'center'}} marginBottom={10}>Anserini Search</Heading>
          </Flex>
          <Flex direction="column" gap={4} height="100%" style={{ width: searchPerformed ? "calc(100% - 100px)" : "100%", marginTop: '30px' }}>
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
          </Flex>
        </Flex>
      </form>
      {searchPerformed && loading && <Spinner size="lg" />}
      {searchPerformed && results.map((result, index) => (
        <Box key={index} p={4} width="calc(100% - 32px)" shadow="md" borderWidth="1px" borderRadius="md">
          <Flex justifyContent="space-between" alignItems="center" direction="row">
            {result.docid && <Text as="h3" fontWeight="bold">
              Document ID: {result.docid}
            </Text>}
            {result.score && <Text as="span" fontWeight="normal">Score: {result.score}</Text>}
          </Flex>
          {result.doc && Object.entries(result.doc).map(([key, value]) => (
            <Text key={key}>
              <strong>{key}:</strong> {
                typeof value === 'object' ? JSON.stringify(value) : (String)(value)
              }
            </Text>
          ))}
        </Box>
      ))}
    </VStack>
  );
};

export default SearchBar;

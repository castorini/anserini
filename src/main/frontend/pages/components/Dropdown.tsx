import React, { useState, useEffect } from 'react';
import { Select, HStack, Box } from '@chakra-ui/react';

interface Props {
  onSelect: (selectedValue: string) => void;
}

interface IndexInfo {
  indexName: string;
  description: string;
  filename: string;
  corpus: string;
  model: string;
  urls: string[];
  md5: string;
  cached: boolean;
}

const Dropdown: React.FC<Props> = ({ onSelect }) => {
  const [selectedCollection, setSelectedCollection] = useState<string | null>(null);
  const [selectedCorpus, setSelectedCorpus] = useState<string | null>(null);
  const [selectedIndex, setSelectedIndex] = useState<string | null>(null);
  const [indexInfoList, setIndexInfoList] = useState<{ [key: string]: IndexInfo }>({});
  const [collections, setCollections] = useState<{ [key: string]: string[] | { [key: string]: string[] } }>({});

  useEffect(() => {
    const fetchIndexes = async () => {
      const response = await fetch('/api/v1.0/indexes');
      const indexList = await response.json();
      setIndexInfoList(indexList);

      const dropdownList: { [key: string]: string[] | { [key: string]: string[] } } = {};
      for (const value of Object.values(indexList)) {
        const index = value as IndexInfo;

        if (index.corpus.includes('MS MARCO')) {
          if (!dropdownList['MS MARCO']) dropdownList['MS MARCO'] = {};
          const msmarco = dropdownList['MS MARCO'] as { [key: string]: string[] };
          const corpus = index.corpus as string;
          if (msmarco[corpus]) {
            (msmarco[corpus] as string[]).push(index.indexName);
          } else {
            msmarco[corpus] = [index.indexName];
          }
        } else if (index.corpus.includes('BEIR')) {
          if (!dropdownList['BEIR']) dropdownList['BEIR'] = {};
          const beir = dropdownList['BEIR'] as { [key: string]: string[] };
          const corpus = index.corpus as string;
          if (beir[corpus]) {
            (beir[corpus] as string[]).push(index.indexName);
          } else {
            beir[corpus] = [index.indexName];
          }
        }
      }
      setCollections(dropdownList);
    };

    fetchIndexes();
  }, []);

  return (
    <Box p={4}>
      <HStack spacing={4}>
        <Select placeholder="Select" onChange={(e) => {
          setSelectedCollection(e.target.value);
          setSelectedCorpus(null);
          setSelectedIndex(null);
        }}>
          {Object.keys(collections).map((collection) => (
            <option key={collection} value={collection}>{collection}</option>
          ))}
        </Select>
        {selectedCollection !== null && (
          <>
            <Select placeholder="Select" onChange={(e) => {
              setSelectedCorpus(e.target.value);
              setSelectedIndex(null);
            }}>
              {selectedCollection && collections[selectedCollection] && Object.keys(collections[selectedCollection]).map((corpus) => (
                <option key={corpus} value={corpus}>{corpus.replace('MS MARCO', '').replace('BEIR: ', '')}</option>
              ))}
            </Select>
            {selectedCorpus && ( 
            <Select placeholder="Select" onChange={(e) => {
              setSelectedIndex(e.target.value);
              onSelect(e.target.value);
            }}>
              {selectedCorpus && !Array.isArray(collections[selectedCollection]) &&
                (collections[selectedCollection] as { [key: string]: string[] })[selectedCorpus].map((index) => (
                  <option key={index} value={index}>
                    {indexInfoList[index].model}
                  </option>
                ))}
            </Select>
            )}
          </>
        )}
      </HStack>
    </Box>
  );
};

export default Dropdown;
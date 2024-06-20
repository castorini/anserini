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

import React, { useState, useEffect } from 'react';
import { Select } from '@chakra-ui/react';

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

        if (index.corpus.includes('MS MARCO V1')) {
          if (!dropdownList['MS MARCO V1']) dropdownList['MS MARCO V1'] = [];
          (dropdownList['MS MARCO V1'] as string[]).push(index.indexName);
        } else if (index.corpus.includes('MS MARCO V2')) {
          if (!dropdownList['MS MARCO V2']) dropdownList['MS MARCO V2'] = [];
          (dropdownList['MS MARCO V2'] as string[]).push(index.indexName);
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
    <div className="dropdowns">
      <Select placeholder="Select" onChange={(e) => {
        setSelectedCollection(e.target.value);
        setSelectedCorpus(null);
        setSelectedIndex(null);
      }}>
        {Object.keys(collections).map((collection) => (
          <option key={collection} value={collection}>{collection}</option>
        ))}
      </Select>

      {selectedCollection && selectedCollection.includes("MS MARCO") && (
        <Select placeholder="Select" onChange={(e) => {
          setSelectedIndex(e.target.value);
          onSelect(e.target.value);
        }}>
          {Array.isArray(collections[selectedCollection]) &&
            (collections[selectedCollection] as string[]).map((index) => (
              <option key={index} value={index}>
                {indexInfoList[index].corpus} | {indexInfoList[index].model}
              </option>
            ))}
        </Select>
      )}

      {selectedCollection === 'BEIR' && (
        <>
          <Select placeholder="Select" onChange={(e) => {
            setSelectedCorpus(e.target.value);
            setSelectedIndex(null);
          }}>
            {selectedCollection && collections[selectedCollection] && Object.keys(collections[selectedCollection]).map((corpus) => (
              <option key={corpus} value={corpus}>{corpus}</option>
            ))}
          </Select>
          <Select placeholder="Select" onChange={(e) => {
            setSelectedIndex(e.target.value);
            onSelect(e.target.value);
          }}>
            {selectedCorpus && !Array.isArray(collections[selectedCollection]) &&
              (collections[selectedCollection] as { [key: string]: string[] })[selectedCorpus].map((index) => (
                <option key={index} value={index}>
                  {indexInfoList[index].corpus} | {indexInfoList[index].model}
                </option>
              ))}
          </Select>
        </>
      )}
    </div>
  );
};

export default Dropdown;
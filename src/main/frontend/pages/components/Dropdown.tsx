import React, { useState, useRef, useEffect } from 'react';

interface Props {
  onSelect: (selectedValue: string) => void;
}

const Dropdown: React.FC<Props> = ({ onSelect }) => {
  const [selectedCollection, setSelectedCollection] = useState<string | null>(null);
  const [selectedCorpus, setSelectedCorpus] = useState<string | null>(null);
  const [selectedIndex, setSelectedIndex] = useState<string | null>(null);

  const [collections, setCollections] = useState<{ [key: string]: string[] | { [key: string]: string[] } }>({
    'MS MARCO V1': [
      'msmarco-v1-passage',
      'msmarco-v1-passage.splade-pp-ed',
      'msmarco-v1-passage.cos-dpr-distil',
      'msmarco-v1-passage.cos-dpr-distil.quantized',
      'msmarco-v1-passage.bge-base-en-v1.5',
      'msmarco-v1-passage.bge-base-en-v1.5.quantized',
      'msmarco-v1-passage.cohere-embed-english-v3.0',
      'msmarco-v1-passage.cohere-embed-english-v3.0.quantized',
    ],
    'MS MARCO V2': [
      'msmarco-v2-passage',
      'msmarco-v2-doc',
      'msmarco-v2-doc-segmented',
      'msmarco-v2.1-doc',
      'msmarco-v2.1-doc-segmented',
    ],
    'BEIR': {}
  });

  // Generate collections for BEIR
  useEffect(() => {
    const keys = [
      'trec-covid',
      'bioasq',
      'nfcorpus',
      'nq',
      'hotpotqa',
      'fiqa',
      'signal1m', 
      'trec-news',
      'robust04',
      'arguana',
      'webis-touche2020',
      'cqadupstack-android',
      'cqadupstack-english',
      'cqadupstack-gaming',
      'cqadupstack-gis',
      'cqadupstack-mathematica',
      'cqadupstack-physics',
      'cqadupstack-programmers',
      'cqadupstack-stats',
      'cqadupstack-tex',
      'cqadupstack-unix',
      'cqadupstack-webmasters',
      'cqadupstack-wordpress',
      'quora',
      'dbpedia-entity',
      'scidocs',
      'fever',
      'climate-fever',
      'scifact',
    ];
    const generatedMap = collections;
    
    keys.forEach(key => {
      const newObj = generatedMap['BEIR'] as { [key: string]: string[] };
      newObj[key] = [
        `${key}.flat`,
        `${key}.multifield`,
        `${key}.splade-pp-ed`,
        `${key}.bge-base-en-v1.5`,
      ];
    });
    setCollections(generatedMap);

    console.log('collections', collections);
  }, []);

  return (
    <div className="dropdowns">
      <select className="dropdown-button" onChange={(e) => {
        setSelectedCollection(e.target.value);
        setSelectedCorpus(null);
        setSelectedIndex(null);
      }}>
        <option value="" className="dropdown-item">Select</option>
        {Object.keys(collections).map((collection) => (
          <option className="dropdown-item" key={collection} value={collection}>{collection}</option>
        ))}
      </select>
      {selectedCollection && selectedCollection.includes("MS MARCO") && <>
        <select className="dropdown-button" onChange={(e) => {
          setSelectedIndex(e.target.value);
          onSelect(e.target.value)}}
        >
          <option value="" className="dropdown-item">Select</option>
          {Array.isArray(collections[selectedCollection])
              && (collections[selectedCollection] as string[]).map((index) => (
            <option className="dropdown-item" key={index} value={index}>{index}</option>
          ))}
        </select>
      </>}
      {selectedCollection=='BEIR' && <>
      <select className="dropdown-button" onChange={(e) => {
        setSelectedCorpus(e.target.value);
        setSelectedIndex(null);
      }}>
        <option value="" className="dropdown-item">Select</option>
        {selectedCollection && collections[selectedCollection] && Object.keys(collections[selectedCollection]).map((corpus) => (
          <option className="dropdown-item" key={corpus} value={corpus}>{corpus}</option>
        ))}
      </select>
      <select className="dropdown-button" onChange={(e) => {
        setSelectedIndex(e.target.value);
        onSelect(e.target.value)}}
      >
        <option value="" className="dropdown-item">Select</option>
        {selectedCorpus && !Array.isArray(collections[selectedCollection])
            && (collections[selectedCollection] as { [key: string]: string[] })[selectedCorpus].map((index) => (
          <option className="dropdown-item" key={index} value={index}>{index}</option>
        ))}
      </select></>}
    </div>
  );
};

export default Dropdown;
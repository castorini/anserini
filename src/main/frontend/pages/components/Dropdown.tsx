import React, { useState, useRef, useEffect } from 'react';

interface Props {
  onSelect: (selectedValue: string) => void;
}

const Dropdown: React.FC<Props> = ({ onSelect }) => {
  const [showDropdown, setShowDropdown] = useState<boolean>(false);
  const [selectedCollection, setSelectedCollection] = useState<string | null>(null);
  const [selectedCorpus, setSelectedCorpus] = useState<string | null>(null);
  const [selectedIndex, setSelectedIndex] = useState<string | null>(null);

  const [collections, setCollections] = useState<{ [key: string]: { [key: string]: string[] } }>({
    'MS MARCO': {
      'v1-passage': [
        'msmarco-v1-passage',
        'msmarco-v1-passage.splade-pp-ed',
        'msmarco-v1-passage.cos-dpr-distil',
        'msmarco-v1-passage.cos-dpr-distil.quantized',
        'msmarco-v1-passage.bge-base-en-v1.5',
        'msmarco-v1-passage.bge-base-en-v1.5.quantized',
        'msmarco-v1-passage.cohere-embed-english-v3.0',
        'msmarco-v1-passage.cohere-embed-english-v3.0.quantized',
      ],
      'v2-passage': [
        'msmarco-v2-passage',
      ],
      'v2-document': [
        'msmarco-v2-doc',
        'msmarco-v2-doc-segmented',
      ],
      'v2.1-document': [
        'msmarco-v2.1-doc',
        'msmarco-v2.1-doc-segmented',
      ],
    },
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
    
    generatedMap['BEIR'] = {};
    keys.forEach(key => {
      generatedMap['BEIR'][key] = [
        `${key}.flat`,
        `${key}.multifield`,
        `${key}.splade-pp-ed`,
        `${key}.bge-base-en-v1.5`,
      ];
    });
    setCollections(generatedMap);

    console.log('collections', collections);
  }, []);

  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setShowDropdown(false);
        setSelectedCollection(null);
        setSelectedCorpus(null);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <div ref={dropdownRef} className="dropdown">
      <button className="dropdown-button"
      onClick={() => setShowDropdown(!showDropdown)}>
        {selectedIndex || 'Select index'}
      </button>
      {showDropdown && (
        <div className="dropdown-menu">
          {Object.keys(collections).map((collection) => (
            <div key={collection} className="dropdown-content">
              <button className="dropdown-item" 
                onClick={() => setSelectedCollection(selectedCollection==collection ? null : collection)}
              >
                {collection}
              </button>
              <div className="sub-dropdown-menu">
              {selectedCollection === collection &&
                Object.keys(collections[collection]).map((corpus) => (
                  <div key={corpus} className="dropdown-content">
                    <button className="dropdown-item" 
                      onClick={() => setSelectedCorpus(selectedCorpus==corpus ? null : corpus)}
                    >
                      {corpus}
                    </button>
                    {selectedCorpus === corpus && 
                    <div className="sub-dropdown-menu">
                      {collections[collection][corpus].map((index) => (
                        <div key={index} className="dropdown-content">
                          <button className="dropdown-item"
                          onClick={() => {
                            setSelectedIndex(index);
                            onSelect(index);
                            onSelect(index);
                            setShowDropdown(false);
                            setSelectedCollection(null);
                            setSelectedCorpus(null);
                          }}>
                            {index}
                          </button>
                        </div>
                      ))}
                    </div>}
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Dropdown;
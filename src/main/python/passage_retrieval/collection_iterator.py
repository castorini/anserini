import os
import json
import time
import datetime

from concurrent.futures import ThreadPoolExecutor
from utils import *
from document_tokenizer import *

import logging
logger = logging.getLogger(__name__)


class JCollection:
    """
    Wrapper for Anserini collection classes, 
    which contain logic for iterating over different collections for documents
    """
    
    def __init__(self, collection_class, collection_path):
        self.collection_class = collection_class
        self.collection_path = collection_path
        self.collection_instance = self._get_collection()     
        self.collection_instance.setCollectionPath(self.collection_path)

    def get_iterator(self, segment_path):
        iter = self.collection_instance.createFileSegment(segment_path)
        return iter
    
    def get_segment_paths(self):
        segment_paths = self.collection_instance.getFileSegmentPaths()
        return segment_paths
            
    def _get_collection(self):
        if self.collection_class == 'TrecCollection':
            return JTrecCollection()
        elif self.collection_class == 'TrecwebCollection':
            return JTrecwebCollection()
        elif self.collection_class == 'JsonCollection':
            return JJsonCollection()
        elif self.collection_class == 'HtmlCollection':
            return JHtmlCollection()
        elif self.collection_class == 'CarCollection':
            return JCarCollection()
        elif self.collection_class == 'ClueWeb09Collection':
            return JClueWeb09Collection()
        elif self.collection_class == 'ClueWeb12Collection':
            return JClueWeb12Collection()
        elif self.collection_class == 'NewYorkTimesCollection':
            return JNewYorkTimesCollection()
        elif self.collection_class == 'TweetCollection':
            return JTweetCollection()
        elif self.collection_class == 'WashingtonPostCollection':
            return JWashingtonPostCollection()
        elif self.collection_class == 'WikipediaCollection':
            return JWikipediaCollection()
        else:
            raise ValueError(collection_class)
            

class JGenerator:
    """
    Wrapper for Anserini generator classes, 
    which contain logic for tranforming / parsing different document types 
    for body contents, and generating Lucene documents (ready for indexing)
    """
    
    def __init__(self, generator_class, args, counters):
        self.generator_class = generator_class
        self.args = args
        self.counters = counters
        self.generator_instance = self._get_generator()
        
    def _get_generator(self):
        if self.generator_class == 'LuceneDocumentGenerator':
            return JLuceneDocumentGenerator(self.args, self.counters)
        elif self.generator_class == 'JsoupGenerator':
            return JJsoupGenerator(self.args, self.counters)
        elif self.generator_class == 'WapoGenerator':
            return JWapoGenerator(self.args, self.counters)
        elif self.generator_class == 'TweetGenerator':
            return JTweetGenerator(self.args, self.counters)
        elif self.generator_class == 'NekoGenerator':
            return JNekoGenerator(self.args, self.counters)
        else:
            raise ValueError(generator_class)
     
          
class IterSegment:
    """
    Similar to IndexCollection.LocalIndexerThread
    But does not perform indexing steps
    And instead calls tokenizer on document contents during iteration
    Then writes segmented results to a JSONCollection
    """
    
    def __init__(self, collection, generator, segment_path, output_path):
        self.output_path = output_path
        self.collection = collection
        self.generator = generator
        self.iter = collection.get_iterator(segment_path)
        self.segment_name = segment_path.getFileName().toString()
        self.doc_count = 0 # to keep track of number of documents parsed
        self.results = [] # list as json array for now
        
    def run(self, tokenizer, raw):
        # get iterator, append json object to docs            
        while self.iter.hasNext():
            try:
                d = self.iter.next()
            except:
                self.generator.counters.skipped.incrementAndGet()
                logger.error("Error fetching iter.next(), skipping...")
                continue
            if not d.indexable():
                self.generator.counters.unindexable.incrementAndGet()
                continue

            # Generate Lucene document, then fetch fields
            try:
                doc = self.generator.generator_instance.createDocument(d)
                if doc is None:
                    self.generator.counters.unindexed.incrementAndGet()
                    continue
                
                id = doc.get('id')
                contents = doc.get('raw') if raw else doc.get('contents')        
                doc = {'id': id, 'contents': contents}     

            except:
                self.generator.counters.skipped.incrementAndGet()
                logger.error("Error generating Lucene document, skipping...")
                continue
                
            # append resulting json to list of documents
            if tokenizer is None:
                self.results.append(doc)            
            else:
                # split document into segments
                try:
                    array = tokenizer(id, contents)
                    self.results += array # merge lists
                except:
                    self.generator.counters.skipped.incrementAndGet()
                    logger.error("Error tokenizing document, skipping...")
                    continue
                
            self.doc_count += 1
        
        if (self.iter.getNextRecordStatus() == JBaseFileSegmentStatus.ERROR):
            self.generator.counters.errors.incrementAndGet()
        
        self.iter.close()
                    
        # count number of full documents parsed
        self.generator.counters.indexed.addAndGet(self.doc_count)
        
        count = len(self.results)
        if (count > 0):
            # write json array to outputdir (either as array of docs, or many arrays of doc tokens)
            with open(os.path.join(self.output_path, '{}.json'.format(self.segment_name)), 'w') as f:
                jsonstr = json.dumps(self.results, separators=(',', ':'), indent=2)
                f.write(jsonstr)
                
            logger.info("Finished iterating over segment: " + 
                        self.segment_name + " with " + 
                        str(count) + " results.")
        else:
            logger.info("No documents parsed from segment: " + self.segment_name)
        

def IterCollection(input_path, collection_class, 
                   generator_class, output_path, 
                   threads=1, tokenize=None, raw=False):
    """
    Parameters
    -----------
    
    input_path: str
        path to directory containing collection
        
    collection_class: str
        specify collection class for Anserini
        
    generator_class: str
        specify generator class for Anserini
        
    output_path: str
        path to create directory and write output json collection
        
    threads: int
        maximum number of threads for concurrent processing 
    
    tokenize: str
        specify option for tokenizing/segmenting each document
        defaults to None which outputs full document 
        
    raw: bool
        defaults to false which uses transformed document text as contents
        if true, uses raw document text as contents

    """
    
    start = time.time()
    logger.info("Begin reading collection.")

    args = JArgs()
    args.input = input_path
    args.index = output_path
    args.threads = threads
    args.collectionClass = collection_class
    args.generatorClass = generator_class
    args.storeRawDocs = True ## to store raw text as an option
    args.dryRun = True ## So that indexing will be skipped
    
    ## Check and create tokenizer
    tokenizer = None
    if tokenize is not None:
        try:
            tokenizer = DocumentTokenizer(tokenize).tokenizer
        except:
            raise ValueError(tokenize)
            
    ## Instantiate IndexCollection Class - create output dir
    ## This is so we can use existing generator logic to create documents
    ## The idea is to skip the indexing steps of Anserini
    indexer = JIndexCollection(args) 
    counters = JCounters(indexer)
    
    collection = JCollection(collection_class, JPaths.get(input_path))
    segment_paths = collection.get_segment_paths().toArray()
    generator = JGenerator(generator_class, args, counters)
        
    with ThreadPoolExecutor(max_workers=threads) as executor:
        for (i, path) in enumerate(segment_paths):
            executor.submit(IterSegment(collection, generator, path, output_path).run, tokenizer, raw)
    
    end = time.time()
    elapsed = end - start
      
    # log counters stored in collection
    print("all threads complete")
    logger.info("# Final Counter Values");
    logger.info("documents:     {:12d}".format(counters.indexed.get()))
    logger.info("empty:       {:12d}".format(counters.empty.get()))
    logger.info("unindexed:   {:12d}".format(counters.unindexed.get()))
    logger.info("unindexable: {:12d}".format(counters.unindexable.get()))
    logger.info("skipped:     {:12d}".format(counters.skipped.get()))
    logger.info("errors:      {:12d}".format(counters.errors.get()))
    
    logger.info("Total duration: %s", str(datetime.timedelta(seconds=elapsed)))


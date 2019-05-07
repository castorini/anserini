import os
import json

from concurrent.futures import ThreadPoolExecutor
from utils import *
from document_tokenizer import *

import logging
logger = logging.getLogger(__name__)



class JCollection:
    
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
        ### Add cases of collection API here
        else:
            raise ValueError(collection_class)
            
class JGenerator:
    
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
        ### Add cases of generator classes here
        else:
            raise ValueError(generator_class)
               
class IterSegment:
    # can make this into a thread later for parallel processing?
    
    def __init__(self, collection, generator, segment_path, output_path):
        self.output_path = output_path
        self.collection = collection
        self.generator = generator
        self.iter = collection.get_iterator(segment_path)
        self.segment_name = segment_path.getFileName().toString()
        self.results = [] #list as json array for now
        
    def run(self, tokenize=None, raw=False):
        print("starting thread: " + self.segment_name)
        # get iterator, append json object to docs
        if tokenize:
            tokenizer = DocumentTokenizer(tokenize).tokenizer
        else:
            tokenizer = None
            
        while self.iter.hasNext():
            try:
                d = self.iter.next()
            except:
                self.generator.counters.skipped.incrementAndGet()
                continue
            if not d.indexable():
                self.generator.counters.unindexable.incrementAndGet()
                continue

            # generate Lucene document, then get fields
            doc = self.generator.generator_instance.createDocument(d)
            if doc is None:
                self.generator.counters.unindexed.incrementAndGet()
                continue
            
            id = doc.get('id')
            contents = doc.get('raw') if raw else doc.get('contents')        
            doc = {'id': id, 'contents': contents} 
            
            # append resulting json to list of documents
            if tokenize is None:
                self.results.append(doc)            
            else:
                # call tokenize on document (TODO)
                # raise NotImplementedError
                array = tokenizer(id, contents)
                self.results += array # merge two lists
        
        if (self.iter.getNextRecordStatus() == JBaseFileSegmentStatus.ERROR) {
          self.generator.counters.errors.incrementAndGet()
        }
        self.iter.close()
        
        # write json array to outputdir (either as array of docs, or many arrays of doc tokens)
        with open(os.path.join(self.output_path, '{}.json'.format(self.segment_name)), 'w') as f:
            jsonstr = json.dumps(self.results, separators=(',', ':'), indent=2)
            f.write(jsonstr)
        
        count = len(self.results)
        logger.info("Finished iterating over segment: " + 
                     self.segment_name + " with " + 
                     str(count) + " results.")
        self.generator.counters.indexed.addAndGet(count)
        


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
    
    args = JArgs()
    args.input = input_path
    args.index = output_path
    args.threads = threads
    args.collectionClass = collection_class
    args.generatorClass = generator_class
    args.storeRawDocs = True
    args.dryRun = True ## So that indexing will be skipped
    
    ## Instantiates IndexCollection Class - creates output dir
    ## This is so we can use existing generator logic to create documents
    ## The idea is to skip the indexing steps of Anserini
    indexer = JIndexCollection(args)
    counters = JCounters(indexer)
    
    collection = JCollection(collection_class, JPaths.get(input_path))
    segment_paths = collection.get_segment_paths().toArray()
    generator = JGenerator(generator_class, args, counters)
    
    print(len(segment_paths))
    
    with ThreadPoolExecutor(max_workers=3) as executor:
        for (i, path) in enumerate(segment_paths):
#            if i > 10: break # for debugging
            # single threaded for now
            # IterSegment(collection, generator, path, output_path).run(tokenize, raw)
            executor.submit(IterSegment(collection, generator, path, output_path).run, tokenize, raw)
    # END    
    # log counters stored in collection
    print("all threads complete")
    logger.info("# Final Counter Values");
    logger.info("indexed:     {:12d}".format(counters.indexed.get()))
    logger.info("empty:       {:12d}".format(counters.empty.get()))
    logger.info("unindexed:   {:12d}".format(counters.unindexed.get()))
    logger.info("unindexable: {:12d}".format(counters.unindexable.get()))
    logger.info("skipped:     {:12d}".format(counters.skipped.get()))
    logger.info("errors:      {:12d}".format(counters.errors.get()))


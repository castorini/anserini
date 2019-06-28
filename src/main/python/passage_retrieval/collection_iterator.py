import os
import json
import time
import datetime
from concurrent.futures import ThreadPoolExecutor
from document_tokenizer import DocumentTokenizer

import sys
sys.path += ['src/main/python']
from pyserini.collection import pycollection
from pyserini.index import pygenerator

import logging
logger = logging.getLogger(__name__)


def IterSegment(fs, generator, output_path, tokenizer, tokenmin, raw):
    """
    Parameters
    ----------
    fs : pycollection.FileSegment
        File segment to iterate over for documents.
        
    generator : pygenerator.Generator
        Generator to apply transforms and create Lucene documents.
        
    output_path : str
        Path to output json file containing passages from the file segment.
    
    tokenizer : DocumentTokenizer
        Tokenizer to call on document contents for passage splitting.
        
    tokenmin : int
        Minimum limit argument (e.g. minword) passed to tokenizer
        
    raw : bool
        True for using raw (FIELD_RAW) document contents,
        False for using parsed (FIELD_BODY) document contents.
    
    """
    results = []
    doc_count = 0
    
    for (i, d) in enumerate(fs):
        
        # Skip unindexable documents
        if not d.indexable:
            logger.error(fs.segment_name + 
                     ": Document not indexable, skipping...")
            fs.collection.counters.unindexable.increment()
            continue
                
        # Generate Lucene document, then fetch fields
        try:
            doc = generator.create_document(d)
            if doc is None:
                logger.warn("Generator did not return document, skipping...")
                fs.collection.counters.skipped.increment()
                continue
            id = doc.get('id')
            contents = doc.get('raw') if raw else doc.get('contents')        
            doc = {'id': id, 'contents': contents}     

        except:
            logger.error("Error generating Lucene document, skipping...")
            fs.collection.counters.skipped.increment()
            continue
            
        # append resulting json to list of documents
        if tokenizer is None:
            results.append(doc)            
        else:
            # split document into segments
            try:
                array = tokenizer.run_tokenizer(id, contents, tokenmin)
                results += array # merge lists
            except:
                fs.collection.counters.skipped.increment()
                logger.error("Error tokenizing document, skipping...")
                continue
            
        doc_count += 1
        
    # count number of full documents parsed
    fs.collection.counters.indexable.increment(doc_count)
    logger.info(fs.segment_name + ": " + str(doc_count) + " documents parsed")
    
    count = len(results)
    if (count > 0):
        # write json array to outputdir 
        # (either as array of docs, or many arrays of doc tokens)
        with open(os.path.join(output_path, 
                               '{}.json'.format(fs.segment_name)), 'w') as f:
            jsonstr = json.dumps(results, separators=(',', ':'), indent=2)
            f.write(jsonstr)
            
        logger.info("Finished iterating over segment: " + 
                    fs.segment_name + " with " + 
                    str(count) + " results.")
    else:
        logger.info("No documents parsed from segment: " + fs.segment_name)
        
    
def IterCollection(input_path, collection_class, 
                   generator_class, output_path, 
                   threads=1, tokenize=None, 
                   tokenmin=0, raw=False):
    """
    Parameters
    ----------
    input_path : str
        Path to input collection.
        
    collection_class : str
        Anserini collection class to use.
        
    generator_class : str
        Anserini generator class to use.
        
    output_path : str
        Path to output resulting json collection.
        
    threads : int
        Maximum number of threads.
        
    tokenize : str
        Name of tokenizer method to use for document-to-passage splitting.
        
    tokenmin : int
        Minimum limit argument (e.g. minword) passed to tokenizer
        
    raw : bool
        True for using raw (FIELD_RAW) document contents,
        False for using parsed (FIELD_BODY) document contents.
        
    """
    start = time.time()
    logger.info("Begin reading collection.")
    
    ## Check and create tokenizer
    tokenizer = None
    if tokenize is not None:
        try:
            tokenizer = DocumentTokenizer(tokenize)
        except:
            raise ValueError(tokenize)

    collection = pycollection.Collection(collection_class, input_path)
    generator = pygenerator.Generator(generator_class)
    
    if not os.path.exists(output_path):
        logger.info("making directory...")
        os.mkdir(output_path)
    
    with ThreadPoolExecutor(max_workers=threads) as executor:    
        for (seg_num, fs) in enumerate(collection):
            executor.submit(IterSegment, fs, generator, output_path, 
                            tokenizer, tokenmin, raw)
    
    end = time.time()
    elapsed = end - start

    print("all threads complete")
    logger.info("# Final Counter Values")
    logger.info("indexable:     {:12d}".format(collection.counters.indexable.value))
    logger.info("unindexable: {:12d}".format(collection.counters.unindexable.value))
    logger.info("skipped:     {:12d}".format(collection.counters.skipped.value))
    logger.info("errors:      {:12d}".format(collection.counters.errors.value))
    
    logger.info("Total duration: %s", str(datetime.timedelta(seconds=elapsed)))
    
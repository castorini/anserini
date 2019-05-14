import os
import json
import time
import datetime

from concurrent.futures import ThreadPoolExecutor
from collection.pycollection import *
from collection.pygenerator import *
from document_tokenizer import *

import logging
logger = logging.getLogger(__name__)

logging.basicConfig(level=logging.DEBUG,
                    filename='json_segment.log',
                    format='%(asctime)s %(name)s %(threadName)s %(levelname)s - %(message)s', 
                    datefmt='%m/%d/%Y %I:%M:%S ')      

def IterSegment(fs, generator, output_path, tokenizer, raw):
    
    results = []
    doc_count = 0
    
    for (i, d) in enumerate(fs):
        # Generate Lucene document, then fetch fields
        try:
            doc = generator.generator.createDocument(d.document)
            if doc is None:
                logger.error("Generator did not return document, skipping...")
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
                array = tokenizer(id, contents)
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
        # write json array to outputdir (either as array of docs, or many arrays of doc tokens)
        with open(os.path.join(output_path, '{}.json'.format(fs.segment_name)), 'w') as f:
            jsonstr = json.dumps(results, separators=(',', ':'), indent=2)
            f.write(jsonstr)
            
        logger.info("Finished iterating over segment: " + 
                    fs.segment_name + " with " + 
                    str(count) + " results.")
    else:
        logger.info("No documents parsed from segment: " + fs.segment_name)
        
    

def IterCollection(input_path, collection_class, 
                   generator_class, output_path, 
                   threads=1, tokenize=None, raw=False):
    
    start = time.time()
    logger.info("Begin reading collection.")
    
    ## Check and create tokenizer
    tokenizer = None
    if tokenize is not None:
        try:
            tokenizer = DocumentTokenizer(tokenize).tokenizer
        except:
            raise ValueError(tokenize)

    collection = Collection(collection_class, input_path)
    generator = Generator(generator_class)
    
    if not os.path.exists(output_path):
        logger.info("making directory...")
        os.mkdir(output_path)
    
    with ThreadPoolExecutor(max_workers=threads) as executor:    
        for (seg_num, fs) in enumerate(collection.segments):
            executor.submit(IterSegment, fs, generator, output_path, tokenizer, raw)
    
    end = time.time()
    elapsed = end - start

    print("all threads complete")
    logger.info("# Final Counter Values");
    logger.info("indexable:     {:12d}".format(collection.counters.indexable.value))
    logger.info("unindexable: {:12d}".format(collection.counters.unindexable.value))
    logger.info("skipped:     {:12d}".format(collection.counters.skipped.value))
    logger.info("errors:      {:12d}".format(collection.counters.errors.value))
    
    logger.info("Total duration: %s", str(datetime.timedelta(seconds=elapsed)))
    

IterCollection('C:/cygwin64/home/Emily/usra/collection/disk45',
               'TrecCollection',
               'JsoupGenerator',
               "C:/cygwin64/home/Emily/usra/output/json_sentences2/",
               1,
               'text_sentencer',
               False)
    



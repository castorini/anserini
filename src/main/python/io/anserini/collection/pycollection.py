from .pyjnius_utils import JCollections, JPaths, cast
from .threading_utils import Counters
import re

import logging
logger = logging.getLogger(__name__)

class Collection:
        
    def __init__(self, collection_class, collection_path):
        self.counters = Counters()
        self.collection_class = collection_class
        self.collection_path = JPaths.get(collection_path)
        self.collection = self._get_collection()     
        self.collection.setCollectionPath(self.collection_path)
        self.collection_iterator = self.collection.iterator()
        
    def _get_collection(self):
        try:
            return JCollections[self.collection_class].value()
        except:
            raise ValueError(self.collection_class)
            
    def __iter__(self):
        return self

    def __next__(self):
        if self.collection_iterator.hasNext():
            fs = self.collection_iterator.next()
            return FileSegment(self, fs, fs.getSegmentPath())
        else:
            raise StopIteration
                
            
            
class FileSegment:

    def __init__(self, collection, segment, segment_path):
        self.collection = collection
        try:
            self.segment = cast(collection.collection.getClass().getName() + 
                                "$Segment", segment)
        except:
            logger.exception("Exception from casting FileSegment type...")
            self.segment = cast("io.anserini.collection.FileSegment", 
                                segment)
            
        self.segment_iterator = self.segment.iterator()
        self.segment_path = segment_path
        self.segment_name = re.sub(r"\\|\/", "-", 
                                   collection.collection_path.relativize(
                                           segment_path).toString())
        

    def __iter__(self):
        return self

    def __next__(self):
        if self.segment.iterator().hasNext():
            d = self.segment.iterator().next()
            return Document(self, d)
        else:
            # log if iteration stopped by error
            if (self.segment.getErrorStatus()):
                logger.error(self.segment_name + 
                             ": Error from segment iteration, stopping...")
                self.collection.counters.errors.increment()

            # stop iteration and log skipped documents
            skipped = self.segment.getSkippedCount()
            if (skipped > 0):
                self.collection.counters.skips.increment(skipped)
                logger.warn(self.segment_name + 
                                 ": " + str(skipped) + " documents skipped")
            self.segment.close()
            raise StopIteration

        
class Document:
    
    def __init__(self, segment, document):
        self.segment = segment
        self.document = document
        self.id = document.id()
        self.indexable = document.indexable()
        try:
            self.contents = document.content()
        except:
            self.contents = document.getContent()
        
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
        self.segment_paths = self.collection.getFileSegmentPaths()
        self.segments = (FileSegment(self, 
                                     self.collection.createFileSegment(path), 
                                     path) for path in self.segment_paths.toArray())
        
    def _get_collection(self):
        try:
            return JCollections[self.collection_class].value()
        except:
            raise ValueError(self.collection_class)
            
            
class FileSegment:

    def __init__(self, collection, segment, segment_path):
        self.collection = collection
        try:
            self.segment = cast(collection.collection.getClass().getName() + 
                                "$FileSegment", segment)
        except:
            logger.exception("Exception from casting FileSegment type...")
            self.segment = cast("io.anserini.collection.BaseFileSegment", 
                                segment)
            
        self.segment_path = segment_path
        self.segment_name = re.sub(r"\\|\/", "-", 
                                   collection.collection_path.relativize(
                                           segment_path).toString())
        

    def __iter__(self):
        return self

    def __next__(self):
        if self.segment.hasNext():
            try:
                d = self.segment.next()
                if not d.indexable():
                    logger.error(self.segment_name + 
                             ": Document not indexable, skipping...")
                    self.collection.counters.unindexable.increment()
                    return self.__next__()
                else:
                    return Document(self, d)
            except:
                logger.error(self.segment_name + 
                             ": Error fetching iter.next(), skipping...")
                self.collection.counters.skipped.increment()
                return self.__next__()
        else:
            if (self.segment.getNextRecordStatus().toString() == 'ERROR'):
                logger.error(self.segment_name + 
                             ": EOF - Error from getNextRecordStatus()")
                self.collection.counters.errors.increment()
                self.segment.close()
                raise StopIteration
            else:
                self.segment.close()
                raise StopIteration

        
class Document:
    
    def __init__(self, segment, document):
        self.segment = segment
        self.document = document
        self.id = document.id()
        try:
            self.contents = document.content()
        except:
            self.contents = document.getContent()
        
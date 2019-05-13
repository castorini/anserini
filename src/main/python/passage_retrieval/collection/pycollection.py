from .pyjnius_utils import *

import logging
logger = logging.getLogger(__name__)

class Collection:
        
    def __init__(self, collection_class, collection_path):
        self.collection_class = collection_class
        self.collection_path = JPaths.get(collection_path)
        self.collection = self._get_collection()     
        self.collection.setCollectionPath(self.collection_path)
        self.segment_paths = self.collection.getFileSegmentPaths()
        self.segments = (FileSegment(self.collection.createFileSegment(path), path) for path in self.segment_paths.toArray())
        
    def _get_collection(self):
        try:
            return getattr(JCollections, collection_class)
        except:
            raise ValueError(collection_class)
            
            
class FileSegment:

    def __init__(self, segment, segment_path):
        self.segment = segment
        self.segment_path = segment_path
        self.segment_name = segment_path.getFileName().toString()

    def __iter__(self):
        return self

    def __next__(self):
        if self.segment.hasNext():
            try:
                d = self.segment.next() 
                return Document(d)
            except:
                logger.error(self.segment_name + 
                             ": Error fetching iter.next(), skipping...")
                return self.__next__()
        else:
            if (self.segment.getNextRecordStatus().toString == 'ERROR'):
                logger.error(self.segment_name + 
                             ": EOF - Error from getNextRecordStatus()")
                raise StopIteration
            else:
                logger.info(self.segment_name + ": EOF reached")
                raise StopIteration

        
class Document:
    
    def __init__(self, document):
        self.document = document
        self.id = document.id()
        self.contents = document.content()
        self.indexable = document.indexable()
    
            

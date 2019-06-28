from ..pyclass import JCollections, JPaths, cast
from ..multithreading import Counters
import re

import logging
logger = logging.getLogger(__name__)

class Collection:
    '''
    Iterable wrapper class for Anserini's DocumentCollection.
            
    Parameters
    ----------
    collection_class : str
        Name of collection class to instantiate
    collection_path : str
        Path to directory containing collection
    '''    
    def __init__(self, collection_class, collection_path):
        self.counters = Counters()
        self.collection_class = collection_class
        self.collection_path = JPaths.get(collection_path)
        self.object = self._get_collection()     
        self.object.setCollectionPath(self.collection_path)
        self.collection_iterator = self.object.iterator()
        
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
    '''
    Iterable wrapper class for Anserini's FileSegment.
            
    Parameters
    ----------
    collection : Collection
        Parent collection of the file segment
    segment : io.anserini.collection.FileSegment
        FileSegment object to create wrapper from
    segment_path : str
        Path to file backing the file segment
    '''  
    def __init__(self, collection, segment, segment_path):
        self.collection = collection
        try:
            self.object = cast(collection.object.getClass().getName() + 
                                '$Segment', segment)
        except:
            logger.exception('Exception from casting FileSegment type...')
            self.object = cast('io.anserini.collection.FileSegment', 
                                segment)
            
        self.segment_iterator = self.object.iterator()
        self.segment_path = segment_path
        self.segment_name = re.sub(r'\\|\/', '-', 
                                   collection.collection_path.relativize(
                                           segment_path).toString())
        
    def __iter__(self):
        return self

    def __next__(self):
        if self.object.iterator().hasNext():
            d = self.object.iterator().next()
            return Document(self, d)
        else:
            # log if iteration stopped by error
            if (self.object.getErrorStatus()):
                logger.error(self.segment_name + 
                             ': Error from segment iteration, stopping...')
                self.collection.counters.errors.increment()

            # stop iteration and log skipped documents
            skipped = self.object.getSkippedCount()
            if (skipped > 0):
                self.collection.counters.skips.increment(skipped)
                logger.warn(self.segment_name + 
                                 ': ' + str(skipped) + ' documents skipped')
            self.object.close()
            raise StopIteration

        
class Document:  
    '''
    Wrapper class for Anserini's SourceDocument.
            
    Parameters
    ----------

    segment : FileSegment
        Parent segment of the source document
    document : io.anserini.collection.SourceDocument
        SourceDocument object to create wrapper from
    ''' 
    def __init__(self, segment, document):
        self.segment = segment
        self.object = document
        self.id = self.object.id()
        self.indexable = self.object.indexable()
        try:
            self.contents = self.object.content()
        except:
            self.contents = self.object.getContent()
        
import os
import json

from utils import *
from document_tokenizer import *

class JCollection:
    
    def __init__(self, collection_class, collection_path, transform=None):
        self.collection_class = collection_class
        self.collection_path = collection_path
        self.transform = transform
        self.collection_instance = self._get_collection()     
        self.collection_instance.setCollectionPath(self.collection_path)

    def get_iterator(self, segment_path):
        iter = self.collection_instance.createFileSegment(segment_path)
        return iter
    
    def get_segment_paths(self):
        segment_paths = self.collection_instance.getFileSegmentPaths()
        return segment_paths

    def get_transform(self):
        if self.transform == 'JsoupStringTransform':
            return JJsoupStringTransform()
        elif self.transform == 'NekoStringTransform':
            return JNekoStringTransform()
        else:
            raise ValueError(transform)
            
    def _get_collection(self):
        if self.collection_class == 'TrecCollection':
            return JTrecCollection()
        elif self.collection_class == 'JsonCollection':
            return JJsonCollection()
        ### Add cases of collection API here
        else:
            raise ValueError(collection_class)

#class Counters:
#    # For multithreading support, re-implement as thread-safe counters
#    def __init__(self):
#        self.added = 0
#        self.empty = 0
#        self.unindexed = 0
#        self.unindexable = 0
#        self.skipped = 0
#        self.errors = 0
               
class IterSegment:
    # can make this into a thread later for parallel processing
    
    def __init__(self, collection, segment_path, output_path, tokenize=None):
        self.output_path = output_path
        self.tokenize = tokenize
        self.collection = collection
        self.iter = collection.get_iterator(segment_path)
        self.segment_name = segment_path.getFileName().toString()
        self.results = [] #list as json array for now
#        self.docs_tokenized = [] #list as json array for now
        
    def run(self):
        # get iterator, append json object to docs
        while self.iter.hasNext():
            try:
                d = self.iter.next()
            except:
                # todo - increment skipped counter
                print("iterator error, skipped")
                continue
            if not d.indexable():
                # case to increment unindexable counter
                print("not indexable, skipped")
                continue
            # append to list of documents
            content = d.content()
            if self.collection.transform is not None:
                content = self.collection.get_transform().apply(d.content())
                
            doc = {'id': d.id(), 'contents': content} 
            # {'fields': d.fields()}
            
            if self.tokenize is None:
                self.results.append(doc)
#                print("Appended: " + d.id()) #debug
            
            else:
                # call tokenize on document (TODO)
                raise NotImplementedError
#                tokenizer = DocumentTokenizer()
#                item = tokenizer.tokenize(doc, collection.collection_class)
#                results += item # merge two lists
        
        print('writing json to file')
        # write json array to outputdir (either as array of docs, or many arrays of doc tokens)
        with open(os.path.join(self.output_path, '{}.json'.format(self.segment_name)), 'w') as f:
            jsonstr = json.dumps(self.results, separators=(',', ':'), indent=2)
            f.write(jsonstr)
            
        print("Finished iterating over segment: " + 
                     self.segment_name + " with " + 
                     str(len(self.results)) + " results.")
        


def IterCollection(input_path, collection_class, output_path, 
                      tokenize_option=None, transform=None):
    """
    input_path: path to folder containing collection
    output_path: path to directory to write output json collection
    tokenize_option: option for tokenizer, None if full document
    threads: multithreading (TODO?)
    collection_class: specify collection class for collections API
    transform: option for string transform, None if raw document
    """
    collection = JCollection(collection_class, input_path, transform)
    segment_paths = collection.get_segment_paths().toArray()
    
    print(len(segment_paths))
    for (i, path) in enumerate(segment_paths):
#        java: executor.execute(new LocalIndexerThread(writer, collection, (Path) segmentPaths.get(i)));
#        thread = IterThread(collection, path).start() #writer?
#        thread.start()
        IterSegment(collection, path, output_path, tokenize_option).run()
    # END    
    # log counters stored in collection
    
def safe_mkdir(path):
    if not os.path.exists(path):
        os.mkdir(path)

############
# Testing
############
collection = JCollection('TrecCollection', 
                         JPaths.get('../../../../../collection/disk45'),
                         'JsoupStringTransform')

segment_paths = collection.get_segment_paths().toArray()
safe_mkdir('json_test/')
IterSegment(collection, segment_paths[0], 'json_test/').run()

#IterCollection(JPaths.get('../../../../../collection/disk45'),
#               'TrecCollection',
#               'json_test/', 
#               None,
#               'JsoupStringTransform')

#test = CollectionIterator()
#iter = test.get_iterator(JPaths.get('../../collection/disk45/disk4/fr94/01/fr940104.0z'), 'TrecCollection')

# collection_path = JPaths.get(input)
# check if exists, is directory, and readable
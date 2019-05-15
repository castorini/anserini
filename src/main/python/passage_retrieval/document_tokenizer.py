from nltk.tokenize import TextTilingTokenizer
from nltk.tokenize import sent_tokenize
from bs4 import BeautifulSoup

import re
import logging

logger = logging.getLogger(__name__)

class DocumentTokenizer:
    """
    Initialize with option (str) specifying what tokenizer to use:
    self.tokenizer(id, contents) returns JSON array of tokenized contents
    """
    def __init__(self, option):
            self.tokenizer = getattr(self, option)
                
    ## Try different tokenizing logic here
    
    ## 1) Use with raw=False, i.e. the parsed FIELD_BODY from lucene Documents
    
    def text_sentencer(self, id, contents):
        # pass in transformed content from document for now
        # using raw content may require different parsing for
        # different document types, e.g. html vs. xml
        results = []
        try:
            tokens = sent_tokenize(contents)
            if (len(tokens) > 100000):
                logger.error(str(len(tokens)) + " tiles from document " + str(id))
                
            for (i, tile) in enumerate(tokens):
                tile_id = '{}.{:06d}'.format(id, i)
                results.append({'id': tile_id, 'contents': tile})
        except:
            # error tokenizing, write as one tile
            logger.error("error tokenizing, write as one tile")
            results.append({'id':'{}.{:06d}'.format(id, 0), 
                            'contents': contents})
            
        return results
    
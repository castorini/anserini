from nltk.tokenize import TextTilingTokenizer
from nltk.tokenize import sent_tokenize
from bs4 import BeautifulSoup

import re
import math
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
    
    def run_tokenizer(self, id, contents, lim):
        results = []
        try:
            tokens = self.tokenizer(id, contents, lim)
            if (len(tokens) > 100000):
                logger.warn(str(len(tokens)) + " tiles from document " + str(id))
                
            for (i, tile) in enumerate(tokens):
                tile_id = '{}.{:06d}'.format(id, i)
                results.append({'id': tile_id, 'contents': tile})
        except:
            # error tokenizing, write as one tile
            logger.exception("Error tokenizing, write as one tile")
            results.append({'id':'{}.{:06d}'.format(id, 0), 
                            'contents': contents})
        return results

    
    def minwords_sentence(self, id, contents, lim):
        # split into sentences, given minimum word limit          
        tokens = []
        passage = []
        words = 0
        for s in sent_tokenize(contents):
            if (words < lim) or (not passage):
                passage.append(s)
                words += len(s.split())
            else:
                token = " ".join(passage)
                tokens.append(token)
                passage = [s]
                words = len(s.split())
                
        token = " ".join(passage)
        if (words >= lim) or (not tokens):
            tokens.append(token)
        else:
            tokens[-1] = " ".join([tokens[-1], token])
            
        return tokens
    
    
    def fixed_word(self, id, contents, lim):
        # split into passages of lim words        
        tokens = []
        words = contents.split()
        length = len(words)
        passages = (words[i:i+lim] for i in range(0, length, lim))
        
        for p in passages: 
            if (len(p) == lim) or (not tokens): 
                tokens.append(" ".join(p))
            else:
                tokens[-1] = " ".join([tokens[-1], " ".join(p)])
        return tokens
    
    
    def word_window(self, id, contents, lim):
        # split into windows of lim words, with each window starting at
        # midpoint of previous window        
        tokens = []
        words = contents.split()
        length = len(words)
        passages = (words[i:i+lim] for i in range(0, 
                    length-math.ceil(lim/2), math.ceil(lim/2)))
        
        for p in passages: 
            if (len(p) >= lim) or (not tokens): 
                tokens.append(" ".join(p))
            else:
                tokens[-1] = " ".join([tokens[-1], " ".join(p)])
        return tokens
    
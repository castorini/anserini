from nltk.tokenize import TextTilingTokenizer
from nltk.tokenize import sent_tokenize
from bs4 import BeautifulSoup

import re
import math
import logging
logger = logging.getLogger(__name__)

class DocumentTokenizer:
    """
    Factory class for applying different tokenizing methods to documents.
            
    Parameters
    ----------
    option : str
        Name of tokenizing method (as defined below) to apply.
        
    Attributes
    ----------
    tokenizer : callable
        Class method for splitting document contents into passages.
        Takes arguments: id, contents, lim
        
    """
    def __init__(self, option):
        self.tokenizer = getattr(self, option)
    
             
    def run_tokenizer(self, id, contents, lim):
        """
        Parameters
        ----------
        id : str
            Document id
        contents : str
            Document contents to tokenize
        lim : int
            Minimum limit argument (e.g. minword) passed to tokenizer
        
        Returns
        -------
        results : list of dict
            JSON array-style passages, with 'id' and 'contents' fields
        
        """
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

    ## Try different tokenizing logic here
    
    ## 1) Use with raw=False, i.e. the parsed FIELD_BODY from lucene Documents
     
    def split_sentence_minword(self, id, contents, lim):
        """
        Split into sentences, and join sentences until passage 
        surpasses a given word length.
        
        Parameters
        ----------
        id : str
            Document id
        contents : str
            Document contents to tokenize
        lim : int
            Minimum number of words per passage, otherwise join sentences
        
        Returns
        -------
        results : list of str
            List of tokenized contents
        
        """
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
    
    
    def split_fixed_minword(self, id, contents, lim):
        """
        Split into passages of a fixed number of (minimum) words.
        
        Parameters
        ----------
        id : str
            Document id
        contents : str
            Document contents to tokenize
        lim : int
            Minimum number of words per passage
        
        Returns
        -------
        results : list of str
            List of tokenized contents
        
        """
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
    
    
    def split_window_minword(self, id, contents, lim):  
        """
        Split into passages from overlapping windows of a fixed number of (min)
        words, with each window starting at midpoint of previous window.
        
        Parameters
        ----------
        id : str
            Document id
        contents : str
            Document contents to tokenize
        lim : int
            Minimum number of words per passage window
        
        Returns
        -------
        results : list of str
            List of tokenized contents
        
        """
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
    
from nltk.tokenize import TextTilingTokenizer
from nltk.tokenize import sent_tokenize
from bs4 import BeautifulSoup
import re
import logging

logger = logging.getLogger(__name__)

class DocumentTokenizer:
    """
    Tokenize:
        initialize with option (str) specifying what tokenizer to use
        self.tokenizer(id, contents) returns JSON array of tokenized contents
    """
    def __init__(self, option):
#        self.option = option
        self.tokenizer = getattr(self, option)

#    def _get_tokenizer(self, option):
#        if option == 'trec_tiler':
#            return self._trec_tiler
#        elif option == 'trec_sentencer':
#            return self._trec_sentencer
#        else:
#            raise ValueError(option)

    def trec_tiler(self, id, contents):
        results = []
        parsed = BeautifulSoup(contents, features="xml").text
        try:
            ttt = TextTilingTokenizer()
            tokens = ttt.tokenize(parsed)
            print(str(len(tokens)) + " tiles from document " + str(id))
            for (i, tile) in enumerate(tokens):
                tile_id = '{}.{:06d}'.format(id, i)
                contents = re.sub('[\n]+', ' ', tile)
                results.append({'id': tile_id, 'contents': contents})
        except:
            # error tokenizing, write as one tile
            print("error tokenizing, write as one tile")
            contents = re.sub('[\n]+', ' ', parsed)
            results.append({'id':'{}.{:06d}'.format(id, 0), 
                            'contents': contents})
            
        return results
    
    def trec_sentencer(self, id, contents):
        results = []
        parsed = re.sub('[\n]+', ' ', BeautifulSoup(contents, features="xml").text)
        try:
            tokens = sent_tokenize(parsed)
            if (len(tokens) > 100000):
                logger.error(str(len(tokens)) + " tiles from document " + str(id))
            for (i, tile) in enumerate(tokens):
                tile_id = '{}.{:06d}'.format(id, i)
                results.append({'id': tile_id, 'contents': tile})
        except:
            # error tokenizing, write as one tile
            print("error tokenizing, write as one tile")
            results.append({'id':'{}.{:06d}'.format(id, 0), 
                            'contents': parsed})
            
        return results

    



from nltk.tokenize import TextTilingTokenizer
from bs4 import BeautifulSoup

class DocumentTokenizer:
    """
    Tokenize: 
        input: document as json object
        return segmented document as json array
    """
    def __init__(self, option):
        self.option = option
        self.tokenizer = self._get_tokenizer(option)

    def _get_tokenizer(self, option):
        if option == 'trec_tiling':
            return self._trec_tiling
        else:
            raise ValueError(option)

    def _trec_tiling(self, id, contents):
        results = []
        parsed = BeautifulSoup(contents, features="xml").text
        try:
            ttt = TextTilingTokenizer()
            tokens = ttt.tokenize(parsed)
            print(str(len(tokens)) + " tiles from document " + str(id))
            for (i, tile) in enumerate(tokens):
                tile_id = '{}.{:05d}'.format(id, i),
                results.append({'id': tile_id, 'contents': tile})
        except:
            # error tokenizing, write as one tile
            print("error tokenizing, write as one tile")
            results.append({'id':'{}.{:05d}'.format(id, 0), 
                            'contents': parsed})
            
        return results
    
#    def _text_tiling(self, id, content):
#        ttt = TextTilingTokenizer()
#        tokens = ttt.tokenize(content)
#        return None
#    
    



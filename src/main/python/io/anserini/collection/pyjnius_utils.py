# Pyjnius setup
import sys
sys.path += ['src/main/python']
from pyjnius_setup import configure_classpath
configure_classpath()

from jnius import autoclass
from jnius import cast

from enum import Enum


JString = autoclass('java.lang.String')
JPath = autoclass('java.nio.file.Path')
JPaths = autoclass('java.nio.file.Paths')
JList = autoclass('java.util.List')


class JIndexHelpers:
    
    def JArgs():
        args = autoclass('io.anserini.index.IndexCollection$Args')()
        args.storeRawDocs = True ## to store raw text as an option
        args.dryRun = True ## So that indexing will be skipped
        return args
    
    def JCounters():
        IndexCollection = autoclass('io.anserini.index.IndexCollection')
        Counters = autoclass('io.anserini.index.IndexCollection$Counters')
        return Counters(IndexCollection)
    

class JCollections(Enum):
    CarCollection = autoclass('io.anserini.collection.CarCollection')
    ClueWeb09Collection = autoclass('io.anserini.collection.ClueWeb09Collection')
    ClueWeb12Collection = autoclass('io.anserini.collection.ClueWeb12Collection')
    HtmlCollection = autoclass('io.anserini.collection.HtmlCollection')
    JsonCollection = autoclass('io.anserini.collection.JsonCollection')
    NewYorkTimesCollection = autoclass('io.anserini.collection.NewYorkTimesCollection')
    TrecCollection = autoclass('io.anserini.collection.TrecCollection')
    TrecwebCollection = autoclass('io.anserini.collection.TrecwebCollection')
    TweetCollection = autoclass('io.anserini.collection.TweetCollection')
    WashingtonPostCollection = autoclass('io.anserini.collection.WashingtonPostCollection')
    WikipediaCollection = autoclass('io.anserini.collection.WikipediaCollection')


class JGenerators(Enum):        
    LuceneDocumentGenerator = autoclass('io.anserini.index.generator.LuceneDocumentGenerator')
    JsoupGenerator = autoclass('io.anserini.index.generator.JsoupGenerator')
    NekoGenerator = autoclass('io.anserini.index.generator.NekoGenerator')
    TweetGenerator = autoclass('io.anserini.index.generator.TweetGenerator')
    WapoGenerator = autoclass('io.anserini.index.generator.WapoGenerator')
    



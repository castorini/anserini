import jnius_config
jnius_config.set_classpath("../../../../target/anserini-0.4.1-SNAPSHOT-fatjar.jar")

from jnius import autoclass
from jnius import cast

###############
# Java Classes
###############

JString = autoclass('java.lang.String')
JPath = autoclass('java.nio.file.Path')
JPaths = autoclass('java.nio.file.Paths')
JList = autoclass('java.util.List')


#######################
# Anserini collections
#######################

class JCollections:
    
    def CarCollection():
        return autoclass('io.anserini.collection.CarCollection')()
    
    def ClueWeb09Collection():
        return autoclass('io.anserini.collection.ClueWeb09Collection')()
    
    def ClueWeb12Collection():
        return autoclass('io.anserini.collection.ClueWeb12Collection')()
    
    def HtmlCollection():
        return autoclass('io.anserini.collection.HtmlCollection')()
    
    def JsonCollection():
        return autoclass('io.anserini.collection.JsonCollection')()
    
    def NewYorkTimesCollection():
        return autoclass('io.anserini.collection.NewYorkTimesCollection')()
    
    def TrecCollection():
        return autoclass('io.anserini.collection.TrecCollection')()
    
    def TrecwebCollection():
        return autoclass('io.anserini.collection.TrecwebCollection')()
    
    def TweetCollection():
        return autoclass('io.anserini.collection.TweetCollection')()
    
    def WashingtonPostCollection():
        return autoclass('io.anserini.collection.WashingtonPostCollection')()
    
    def WikipediaCollection():
        return autoclass('io.anserini.collection.WikipediaCollection')()


###############################
# Anserini document generators
###############################

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
    
    
class JGenerators:        
    
    def LuceneDocumentGenerator(args, counter):
        return autoclass('io.anserini.index.generator.LuceneDocumentGenerator')(args, counter)
    
    def JsoupGenerator(args, counter):
        return autoclass('io.anserini.index.generator.JsoupGenerator')(args, counter)
    
    def NekoGenerator(args, counter):
        return autoclass('io.anserini.index.generator.NekoGenerator')(args, counter)
    
    def TweetGenerator(args, counter):
        return autoclass('io.anserini.index.generator.TweetGenerator')(args, counter)
    
    def WapoGenerator(args, counter):
        return autoclass('io.anserini.index.generator.WapoGenerator')(args, counter)
    
    
#class JTransforms:
#    
#    def JsoupStringTransform():
#        return autoclass('io.anserini.imdex.transform.JsoupStringTransform')()
#    
#    def NekoStringTransform():
#        return autoclass('io.anserini.imdex.transform.NekoStringTransform')()
    





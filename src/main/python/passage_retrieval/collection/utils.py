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


############################
# Anserini counters and args 
############################

#JArgs = autoclass('io.anserini.index.IndexCollection$Args')
#JCounters = autoclass('io.anserini.index.IndexCollection$Counters')
#JIndexCollection = autoclass('io.anserini.index.IndexCollection')


#######################
# Anserini collections
#######################

class JCollections:
    
    def CarCollection():
        return autoclass('io.anserini.collection.CarCollection')
    
    def ClueWeb09Collection():
        return autoclass('io.anserini.collection.ClueWeb09Collection')
    
    def ClueWeb12Collection():
        return autoclass('io.anserini.collection.ClueWeb12Collection')
    
    def HtmlCollection():
        return autoclass('io.anserini.collection.HtmlCollection')
    
    def JsonCollection():
        return autoclass('io.anserini.collection.JsonCollection')
    
    def NewYorkTimesCollection():
        return autoclass('io.anserini.collection.NewYorkTimesCollection')
    
    def TrecCollection():
        return autoclass('io.anserini.collection.TrecCollection')
    
    def TrecwebCollection():
        return autoclass('io.anserini.collection.TrecwebCollection')
    
    def TweetCollection():
        return autoclass('io.anserini.collection.TweetCollection')
    
    def WashingtonPostCollection():
        return autoclass('io.anserini.collection.WashingtonPostCollection')
    
    def WikipediaCollection():
        return autoclass('io.anserini.collection.WikipediaCollection')


###############################
# Anserini document generators
###############################

#JLuceneDocumentGenerator = autoclass('io.anserini.index.generator.LuceneDocumentGenerator')
#JJsoupGenerator = autoclass('io.anserini.index.generator.JsoupGenerator')
#JNekoGenerator = autoclass('io.anserini.index.generator.NekoGenerator')
#JTweetGenerator = autoclass('io.anserini.index.generator.TweetGenerator')
#JWapoGenerator = autoclass('io.anserini.index.generator.WapoGenerator')


import jnius_config
jnius_config.set_classpath("target/anserini-0.4.1-SNAPSHOT-fatjar.jar")

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

JArgs = autoclass('io.anserini.index.IndexCollection$Args')
JCounters = autoclass('io.anserini.index.IndexCollection$Counters')
JIndexCollection = autoclass('io.anserini.index.IndexCollection')


#######################
# Anserini collections
#######################

JBaseFileSegmentStatus = autoclass('io.anserini.collection.BaseFileSegment$Status')

JCarCollection = autoclass('io.anserini.collection.CarCollection')
JClueWeb09Collection = autoclass('io.anserini.collection.ClueWeb09Collection')
JClueWeb12Collection = autoclass('io.anserini.collection.ClueWeb12Collection')
JHtmlCollection = autoclass('io.anserini.collection.HtmlCollection')
JJsonCollection = autoclass('io.anserini.collection.JsonCollection')
JNewYorkTimesCollection = autoclass('io.anserini.collection.NewYorkTimesCollection')
JTrecCollection = autoclass('io.anserini.collection.TrecCollection')
JTrecwebCollection = autoclass('io.anserini.collection.TrecwebCollection')
JTweetCollection = autoclass('io.anserini.collection.TweetCollection')
JWashingtonPostCollection = autoclass('io.anserini.collection.WashingtonPostCollection')
JWikipediaCollection = autoclass('io.anserini.collection.WikipediaCollection')


###############################
# Anserini document generators
###############################

JLuceneDocumentGenerator = autoclass('io.anserini.index.generator.LuceneDocumentGenerator')
JJsoupGenerator = autoclass('io.anserini.index.generator.JsoupGenerator')
JNekoGenerator = autoclass('io.anserini.index.generator.NekoGenerator')
JTweetGenerator = autoclass('io.anserini.index.generator.TweetGenerator')
JWapoGenerator = autoclass('io.anserini.index.generator.WapoGenerator')


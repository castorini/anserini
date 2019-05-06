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

JTrecCollection = autoclass('io.anserini.collection.TrecCollection')
JJsonCollection = autoclass('io.anserini.collection.JsonCollection')
# add more ...


####################################
# Anserini transforms and generators
####################################

JLuceneDocumentGenerator = autoclass('io.anserini.index.generator.LuceneDocumentGenerator')
JJsoupGenerator = autoclass('io.anserini.index.generator.JsoupGenerator')
JNekoGenerator = autoclass('io.anserini.index.generator.NekoGenerator')
JTweetGenerator = autoclass('io.anserini.index.generator.TweetGenerator')
JWapoGenerator = autoclass('io.anserini.index.generator.WapoGenerator')


JJsoupStringTransform = autoclass('io.anserini.index.transform.JsoupStringTransform')
JNekoStringTransform = autoclass('io.anserini.index.transform.NekoStringTransform')



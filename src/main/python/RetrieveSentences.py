import jnius_config
import argparse
import os

#os.environ["CLASSPATH"] = "/Users/mengfeiliu/Documents/E2EQA/Anserini_IJ/Anserini/target/anserini-0.0.1-SNAPSHOT-jar-with-dependencies.jar"
os.environ["CLASSPATH"] = "./target/anserini-0.0.1-SNAPSHOT-jar-with-dependencies.jar"
#os.environ["CLASSPATH"] = "/Users/mengfeiliu/Documents/E2EQA/Anserini_IJ/Anserini/src/main/python/anserini-0.0.1-SNAPSHOT.jar"
#classPath = "/Users/mengfeiliu/Documents/E2EQA/Anserini_IJ/Anserini/classes/artifacts/anserini_jar/anserini.jar" #To be changed later
#jnius_config.set_classpath(classPath)

#print(jnius_config.classpath)


from jnius import autoclass

#System = autoclass("java.lang.System")
#Math = autoclass("java.lang.Math")
#System.out.println("abc")
#System.out.println(Math.abs(-3))
#print(System.getProperty('java.class.path'))
#test = autoclass("org.apache.lucene.index.DirectoryReaders")
RetrieveSentences = autoclass("io.anserini.qa.RetrieveSentences")
test = RetrieveSentences.test()


#TweetAnalyzer = autoclass("io/anserini/analysis/TweetAnalyzer.class")
#RetrieveSentences = autoclass("io.anserini.qa.RetrieveSentences") #To be changed
#rs = RetrieveSentences()






# if __name__ == "__main__":
#     parser = argparse.ArgumentParser(description='Retrieve Sentences')
#     parser.add_argument("-index", help="Lucene index", required=True)
#     parser.add_argument("-embeddings", help="Path of the word2vec index", default="")
#     parser.add_argument("-topics", help="topics file", default="")
#     parser.add_argument("-query", help="a single query", default="")
#     parser.add_argument("-hits", help="max number of hits to return", default=100)
#     parser.add_argument("-scorer", help="passage scores", default="Idf")
#     parser.add_argument("-k", help="top-k passages to be retrieved", default=1)
#
#     args = parser.parse_args()
#     rs = RetrieveSentences(args)
#     rs.getRankedPassages(args)
#
#


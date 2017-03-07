from py4j.java_gateway import JavaGateway

gateway = JavaGateway()
searcher = gateway.entry_point.getSearcher()

# parse out the command line call
index = gateway.jvm.java.lang.String("file:///home/s43moham/indexes/lucene-index.TrecQA.pos+docvectors+rawdocs/")
bm25 = True
topicreader = gateway.jvm.java.lang.String("Trec")
topics = gateway.jvm.java.lang.String("file:///home/s43moham/dev/Anserini/src/main/resources/topics-and-qrels/topics.51-100.txt")
output = gateway.jvm.java.lang.String("file:///home/s43moham/dev/Anserini/output run.disk12.51-100.bm25.txt")


useQueryParser = False
keepstop = False
hits = 1000
k1 = 0.9
b = 0.4


similarity = gateway.jvm.org.apache.lucene.search.similarities.BM25Similarity(k1, b);
# gateway.help(similarity)

pathlist = gateway.jvm.java.util.ArrayList()
pathlist.append(index)
uri = gateway.jvm.java.net.URI(index)
paths = gateway.jvm.java.nio.file.Paths
dir = gateway.jvm.org.apache.lucene.store.FSDirectory.open(paths.get(uri))


cascade = gateway.jvm.io.anserini.rerank.RerankerCascade()
identity_ranker = gateway.jvm.io.anserini.rerank.IdentityReranker()
cascade.add( identity_ranker )

# tr = gateway.jvm.io.anserini.search.query.TrecTopicReader(path)
# topics = tr.read()

# searcher.search()

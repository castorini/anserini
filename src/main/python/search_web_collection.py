from py4j.java_gateway import JavaGateway

gateway = JavaGateway()

index = gateway.jvm.java.lang.String("/home/s43moham/indexes/lucene-index.TrecQA.pos+docvectors+rawdocs/")
searcher = gateway.jvm.io.anserini.search.SearchWebCollection(index)

# query = "Airbus Subsidies"
# hits = 30
# gateway.help(searcher)
def search(query_string, num_hits):
    docids = searcher.search(query_string, num_hits)
    return docids
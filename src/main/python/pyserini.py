from py4j.java_gateway import JavaGateway

gateway = JavaGateway()
index = gateway.jvm.java.lang.String("/home/s43moham/indexes/lucene-index.TrecQA.pos+docvectors+rawdocs/")
pyserini = gateway.jvm.io.anserini.py4j.PyseriniEntryPoint()
pyserini.initializeWithIndex(index)

# query = "Airbus Subsidies"
# hits = 30
# gateway.help(pyserini)
def search(query_string, num_hits):
    docids = pyserini.search(query_string, num_hits)
    return docids

# docid = "FT943-5123"
def raw_doc(docid):
    doc_text = pyserini.getRawDocument(docid)
    return doc_text

def ranked_passages(query_string, num_hits=20, k=10):
    passages = pyserini.getRankedPassages(query_string, num_hits, k)
    return passages
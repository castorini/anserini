import argparse

from py4j.java_gateway import JavaGateway

class Pyserini:
    """Common base class for all methods accessing Anserini (Java)
    Attributes
    ----------
    gateway : :obj:`JavaGateway`
        The Java Gatewat object.
    index : str
        The directory path to the index.
    pyserini : :obj:`PyseriniEntryPoint`
        The entry point to the Java class PyseriniEntryPoint.
    """

    def __init__(self, index_path):
        """
           Constructor for the Pyserini class.

           Parameters
           ----------
           index_path : str
               The directory path for the Lucene index.
        """
        self.gateway = JavaGateway()
        self.index = self.gateway.jvm.java.lang.String(index_path)
        self.pyserini = self.gateway.jvm.io.anserini.py4j.PyseriniEntryPoint()
        self.pyserini.initializeWithIndex(index_path)

    def search(self, query_string, num_hits=20):
        """
           Returns a list of document IDs of documents that matched
           the query_string in the index.

           Parameters
           ----------
            query_string : str
               The query to be searched in the index.
            num_hits : int
               The number of document IDs to be returned in the list.

           Returns
           -------
           :obj:`list` of :obj:`str`
               A list of document IDs that matched the query.
        """
        docids = self.pyserini.search(query_string, num_hits)
        return docids

    def raw_doc(self, docid):
        """
           Returns the raw text from the document given by the docid.

           Parameters
           ----------
            docid : str
               Document ID.

           Returns
           -------
            str
               A list of document IDs that matched the query.
        """
        doc_text = self.pyserini.getRawDocument(docid)
        return doc_text

    def ranked_passages(self, query_string, num_hits=20, k=10):
        """
           Returns the top k ranked passages from the index that matched
           the query_string. First, documents are retrieved and then top
           sentences are retrieved from those documents.

           Parameters
           ----------
            query_string : str
               The query to be searched in the index.
            num_hits : int
               The number of document IDs to be returned by the document retriever.
            k : int
               The number of passages to be returned.

           Returns
           -------
            :obj:`list` of :obj:`str`
               A list of top k passages that matched the query.
        """
        passages = self.pyserini.getRankedPassages(query_string, num_hits, k)
        return passages


    def get_all_sentences(self, query_string, num_hits):
        sentences = self.pyserini.getAllSentences(query_string, int(num_hits))
        return sentences

    def get_term_idf_json(self):
        json_object = self.pyserini.getTermIdfJSON()
        return json_object

if __name__ == "__main__":
    """Test out the Pyserini class."""
    parser = argparse.ArgumentParser(description='Test Pyserini class.')
    parser.add_argument('--index', help='directory path for index', required=True, type=str)
    args = parser.parse_args()
    pyserini = Pyserini(args.index)
    # gateway.help(pyserini)

    search_results = pyserini.search(query_string="Airline Subsidies", num_hits=30)
    print("Search Results:\n{}\n".format(search_results))

    doc_text = pyserini.raw_doc(docid="FT943-5123")
    print("Document Text:\n{}\n".format(doc_text))

    passages = pyserini.ranked_passages(query_string="Airline Subsidies", num_hits=30, k=20)
    print("Ranked Passages:\n{}\n".format(passages))

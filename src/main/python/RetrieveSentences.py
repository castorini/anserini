import jnius_config
import argparse

jnius_config.set_classpath("./target/anserini-0.0.1-SNAPSHOT.jar")

from jnius import autoclass


class CallRetrieveSentences:
    """Python class built to call RetrieveSentences
    Attributes
    ----------
    rs : io.anserini.qa.RetrieveSentences
        The RetrieveSentences object
    args : io.anserini.qa.RetrieveSentences$Args
        The arguments for constructing RetrieveSentences object as well as calling getRankedPassages

    """

    def __init__(self, args):
        """
        Constructor for the CallRetrieveSentences class.

        Parameters
        ----------
        args : argparse.Namespace
            Arguments needed for constructing an instance of RetrieveSentences class
        """
        RetrieveSentences = autoclass("io.anserini.qa.RetrieveSentences")
        Args = autoclass("io.anserini.qa.RetrieveSentences$Args")
        String = autoclass("java.lang.String")

        self.args = Args()
        index = String(args.index)
        self.args.index = index
        embeddings = String(args.embeddings)
        self.args.embeddings = embeddings
        topics = String(args.topics)
        self.args.topics = topics
        query = String(args.query)
        self.args.query = query
        self.args.hits = int(args.hits)
        scorer = String(args.scorer)
        self.args.scorer = scorer
        self.args.k = int(args.k)

        self.rs = RetrieveSentences(self.args)




    def getRankedPassages(self):
        """
        Call RetrieveSentneces.getRankedPassages
        """
        self.rs.getRankedPassages(self.args)



if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Retrieve Sentences')
    parser.add_argument("-index", help="Lucene index", required=True)
    parser.add_argument("-embeddings", help="Path of the word2vec index", default="")
    parser.add_argument("-topics", help="topics file", default="")
    parser.add_argument("-query", help="a single query", default="")
    parser.add_argument("-hits", help="max number of hits to return", default=100)
    parser.add_argument("-scorer", help="passage scores", default="Idf")
    parser.add_argument("-k", help="top-k passages to be retrieved", default=1)


    args_raw = parser.parse_args()
    rs = CallRetrieveSentences(args_raw)
    rs.getRankedPassages()





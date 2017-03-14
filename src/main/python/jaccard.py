class Jaccard():
    """Class containing methods for computing Jaccard similarity between two sentences based on character n-gram overlap."""

    @staticmethod
    def text2ngrams(text, n=3):
        """Convert text into character n-grams."""
        return [text[i:i+n] for i in range(len(text)-n+1)]

    @staticmethod
    def jaccard_overlap(text1, text2):
        """
        Converts the text to character n-gram set and computes Jaccard overlap.
        Calculates Jaccard overlap scores between two texts. 
        """
        set1 = set(Jaccard.text2ngrams(text1, n=3))
        set2 = set(Jaccard.text2ngrams(text2, n=3))
        intersection = set1.intersection(set2)
        union = set1.union(set2)
        return (len(intersection) / float(len(union)))

    @staticmethod
    def most_similar_passage(query, candidate_passages):
        """Returns the passage with the highest Jaccard overlap with the query."""
        best_sim = -1
        best_passage = ""
        for passage in candidate_passages:
            sim = Jaccard.jaccard_overlap(query, passage)
            if sim > best_sim:
                best_sim = sim
                best_passage = passage
        return best_passage


if __name__ == "__main__":
    """Test out the Jaccard class."""
    t1 = "hello, how are you?"
    t2 = "hello, howe are you Brian?"
    t3 = "hi how have you been?"
    print("Text 1: %s" % t1)
    print("Text 2: %s" % t2)
    print("Text 3: %s\n" % t3)

    jaccard = Jaccard()
    print("Jaccard overlap between texts 1 and 2: %f" % jaccard.jaccard_overlap(t1, t2))
    print("Jaccard overlap between texts 1 and 3: %f" % jaccard.jaccard_overlap(t1, t3))
    print("Most similar passage to text 1 (query): %s" % jaccard.most_similar_passage(t1, [t2, t3]))

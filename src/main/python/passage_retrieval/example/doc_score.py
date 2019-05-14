import pandas as pd
import numpy as np


def doc_score(input_path, output_path):
    data = pd.read_csv(input_path, sep=" ", header = None)
    data.columns = ["topic", "query", "id", "rank", "score", "tag"]
    new = data["id"].str.split(".", n=1, expand=True)
    data["docid"] = new[0] # get source document id
#    data["segid"] = new[1] # get segment id
    data = data.drop("id", axis=1)
    
    
    doc = data.groupby(["topic", "query", "tag", "docid"], 
                       as_index=False).agg({"score": np.max, "rank": np.min})
    
    # idea: 
    # rank() over(partition by topic, query, tag order by score desc, rank asc)
    doc = doc.sort_values(["topic", "query", "tag", "score", "rank"], 
                          ascending=[True, True, True, False, True])
    
    doc["rank1"] = doc.groupby(["topic", "query", "tag"])["score"]\
    .rank(method="first", ascending=False).astype(int)
    
    sorted = doc.sort_values(["topic", "query", "tag", "rank1"])
    sorted["round_score"] = round(sorted["score"], 6)
    
    sorted.to_csv(
            output_path, 
            columns=["topic", "query", "docid", "rank1", "round_score", "tag"], 
            sep=" ", header = None, index=False)
    
    
doc_score('../../../../../run.robust04_sentences.bm25.301-450.601-700.txt', 
          '../../../../../run.robust04_sentences_maxscore.bm25.topics.robust04.301-450.601-700.txt')

doc_score('../../../../../run.robust04_sentences.ql.301-450.601-700.txt', 
          '../../../../../run.robust04_sentences_maxscore.ql.topics.robust04.301-450.601-700.txt')

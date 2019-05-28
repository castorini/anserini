import pandas as pd
import numpy as np
import argparse

def get_data(input_path):
    """
    Reading passage retrieval results into a dataframe.
    
    Parameters
    ----------
    input_path : str
        Path to file containing passage retrieval results.
        
    Returns
    -------
    data : DataFrame
        DataFrame containing passage retrieval results.
    
    """
    data = pd.read_csv(input_path, sep=" ", header = None,
                       names=["topic", "query", "id", "rank", "score", "tag"],
                       dtype={"topic": "object", 
                              "query": "object", 
                              "id": "object",
                              "rank": "int64",
                              "score": "float64",
                              "tag": "object"})
    
    new = data["id"].str.split(".", n=1, expand=True)
    data["docid"] = new[0] # get source document id
    data = data.drop("id", axis=1)
    return data


def max_score(input_path, output_path):
    """
    Aggregates passage scores into document scores by taking the 
    maximum scoring passage for each document.
    
    Writes aggregated document results to file.
    
    Parameters
    ----------
    input_path : str
        Path to file containing passage retrieval results.
        
    output_path : str
        Path to output aggregated document-level results.    
    """
    data = get_data(input_path)
    
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
    sorted["tag"] = "max_score"
    
    sorted.to_csv(
            output_path, 
            columns=["topic", "query", "docid", "rank1", "round_score", "tag"], 
            sep=" ", header = None, index=False)


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument("--input", '-i', type=str,
                        help='path to input score file', required=True)
    parser.add_argument("--output", '-o', type=str,
                    help='path to output score file', required=True)    
    parser.add_argument("--method", '-m', type=str,
                    help='score combination method to use', required=True)

    args = parser.parse_args()
    
    try:
        scorer = locals()[args.method]
    except:
        raise ValueError(args.method)
        
    scorer(args.input, args.output)
    
    
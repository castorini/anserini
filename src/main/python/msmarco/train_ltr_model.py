import argparse
import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import cross_validate
import pickle

        
def dict_to_df(d):
    df = pd.DataFrame(d.items())
    df.set_index(0, inplace=True)
    return df
    

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Train LTR model for MSMARCO')
    parser.add_argument('--features', required=True, default='', help='ltr extracted train features file')
    parser.add_argument('--output', required=True, default='', help='output model')

    args = parser.parse_args()

    rows = []
    with open(args.features, 'r', encoding='UTF-8') as fin:
        for line in fin:
            if not line:
                break
            if "#" in line:
                line = line[:line.index("#")]
            if line == "":
                continue
            splits = line.strip().split(" ")
            row = dict()
            row['grade'] = splits[0]
            for i in range(1, len(splits)):
                inner_splits = splits[i].split(":")
                if inner_splits[0] != 'qid':
                    row[inner_splits[0]] = inner_splits[1]
            rows.append(row)
    df = pd.DataFrame(rows)
    y = df['grade']
    X = df.drop(['grade'], axis=1)
    model = LogisticRegression(solver='lbfgs')
    cv_results = cross_validate(model, X, y, cv=5, scoring=('r2', 'neg_mean_squared_error'))
    print(cv_results)
    with open(args.output, 'wb') as fout:
        pickle.dump(model, fout)

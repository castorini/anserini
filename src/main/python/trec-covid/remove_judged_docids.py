"""
Anserini: A toolkit for reproducible information retrieval research built on Lucene

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

import argparse
from collections import defaultdict


def read_qrels(qrel_file):
    qrels = defaultdict(set)
    with open(qrel_file) as f:
        for line in f:
            cols = line.split()
            qid = cols[0]
            docid = cols[2]
            qrels[qid].add(docid)
    return qrels


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('qrel_file')
    parser.add_argument('src_run_file')
    parser.add_argument('dest_run_file')
    parser.add_argument(
        '--k', type=int, default=1000,
        help='the number of results to keep per-topic'
    )
    args=parser.parse_args()

    qrels = read_qrels(args.qrel_file)
    counts = defaultdict(int)
    with open(args.dest_run_file, 'w') as of:
        with open(args.src_run_file) as f:
            for line in f:
                cols = line.split()
                qid = cols[0]

                if counts[qid] >= args.k:
                    continue
                docid = cols[2]

                if (
                    qid not in qrels
                    or docid not in qrels[qid]
                ):
                    counts[qid] += 1
                    of.write(line)


if __name__=='__main__':
    main()
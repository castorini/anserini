#
# Pyserini: Python interface to the Anserini IR toolkit built on Lucene
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import argparse
from collections import defaultdict


def read_file(file):
    docids = set()
    with open(file) as f:
        for line in f:
            cols = line.split()
            if len(cols) > 0:
                docids.add(cols[0])
    return docids


def main():
    parser = argparse.ArgumentParser(description="Filter a TREC run.")
    parser.add_argument('--whitelist', type=str, metavar='docids', help='qrels', required=True)
    parser.add_argument('--input', type=str, metavar='run', help='input run', required=True)
    parser.add_argument('--output', type=str, metavar='run', help='output run', required=True)
    parser.add_argument('--runtag', type=str, default=None, metavar='runtag', help='run tag')
    parser.add_argument('--k', type=int, default=1000, help='the number of results to keep per topic')
    args = parser.parse_args()

    docids = read_file(args.whitelist)
    print(f'Read {len(docids)} docids from {args.whitelist}')
    counts = defaultdict(int)

    prev_score = None
    check_score = True
    with open(args.output, 'w') as output_f:
        with open(args.input) as input_f:
            for line in input_f:
                cols = line.split()
                qid = cols[0]
                docid = cols[2]
                score = float(cols[4])
                tag = cols[5]

                if args.runtag:
                    tag = args.runtag

                if counts[qid] >= args.k:
                    if check_score:
                        if score == prev_score:
                            print(f'Warning: scores of {qid} do not strictly decrease at {docid}')
                        check_score = False
                        continue
                    else:
                        continue

                if docid in docids:
                    counts[qid] += 1
                    prev_score = float(cols[4])
                    check_score = True
                    output_f.write(f'{qid} Q0 {docid} {counts[qid]} {score} {tag}\n')


if __name__ == '__main__':
    main()

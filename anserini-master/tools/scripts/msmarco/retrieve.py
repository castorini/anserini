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

# IMPORTANT NOTE: As of January 2021, this script is *DEFUNCT*.
# That is, you *SHOULDN'T* be using it anymore.
# For "standard" batch retrieval runs on MS MARCO, use 'python -m pyserini.search ...'.

import sys
import argparse
import time

# We're going to explicitly use a local installation of Pyserini (as opposed to a pip-installed one).
# Comment these lines out to use a pip-installed one instead.
sys.path.insert(0, './')
sys.path.insert(0, '../pyserini/')

from pyserini.search import SimpleSearcher


if __name__ == '__main__':
    print(f'WARNING: this script is defunct. Use python -m pyserini.search instead.')

    parser = argparse.ArgumentParser(description='Retrieve MS MARCO Passages.')
    parser.add_argument('--queries', required=True, default='', help='Queries file.')
    parser.add_argument('--output', required=True, default='', help='Output run file.')
    parser.add_argument('--index', required=True, default='', help='Index path.')
    parser.add_argument('--hits', default=10, type=int, help='Number of hits to retrieve.')
    parser.add_argument('--k1', default=0.82, type=float, help='BM25 k1 parameter.')
    parser.add_argument('--b', default=0.68, type=float, help='BM25 b parameter.')
    # See our MS MARCO documentation to understand how these parameter values were tuned.
    parser.add_argument('--rm3', action='store_true', default=False, help='Use RM3.')
    parser.add_argument('--fbTerms', default=10, type=int, help='RM3: number of expansion terms.')
    parser.add_argument('--fbDocs', default=10, type=int, help='RM3: number of documents.')
    parser.add_argument('--originalQueryWeight', default=0.5, type=float, help='RM3: weight of original query.')
    parser.add_argument('--threads', default=1, type=int, help='Maximum number of threads.')

    args = parser.parse_args()

    total_start_time = time.time()

    searcher = SimpleSearcher(args.index)
    searcher.set_bm25(args.k1, args.b)
    print(f'Initializing BM25, setting k1={args.k1} and b={args.b}', flush=True)
    if args.rm3:
        searcher.set_rm3(args.fbTerms, args.fbDocs, args.originalQueryWeight)
        print(f'Initializing RM3, setting fbTerms={args.fbTerms}, fbDocs={args.fbDocs}, ' +
              f' and originalQueryWeight={args.originalQueryWeight}', flush=True)

    if args.threads == 1:

        with open(args.output, 'w') as fout:
            start_time = time.time()
            for line_number, line in enumerate(open(args.queries, 'r', encoding='utf8')):
                qid, query = line.strip().split('\t')
                hits = searcher.search(query, args.hits)
                if line_number % 100 == 0:
                    time_per_query = (time.time() - start_time) / (line_number + 1)
                    print(f'Retrieving query {line_number} ({time_per_query:0.3f} s/query)', flush=True)
                for rank in range(len(hits)):
                    docno = hits[rank].docid
                    fout.write('{}\t{}\t{}\n'.format(qid, docno, rank + 1))
    else:
        qids = []
        queries = []
        result_dict = {}

        for line_number, line in enumerate(open(args.queries, 'r', encoding='utf8')):
            qid, query = line.strip().split('\t')
            qids.append(qid)
            queries.append(query)

        results = searcher.batch_search(queries, qids, args.hits, args.threads)

        with open(args.output, 'w') as fout:
            for qid in qids:
                hits = results.get(qid)
                for rank in range(len(hits)):
                    docno = hits[rank].docid
                    fout.write(f'{qid}\t{docno}\t{rank+1}\n')

    total_time = (time.time() - total_start_time)
    print(f'Total retrieval time: {total_time:0.3f} s')
    print('Done!')

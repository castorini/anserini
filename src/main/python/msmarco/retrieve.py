# -*- coding: utf-8 -*-
'''
Anserini: A Lucene toolkit for replicable information retrieval research

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
'''

import argparse
import time

# Pyserini setup
import os, sys
sys.path += ['src/main/python']
from pyserini.search import pysearch

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Retrieve MS MARCO Passages.')
    parser.add_argument('--qid_queries', required=True, default='', help='query id - query mapping file')
    parser.add_argument('--output', required=True, default='', help='output filee')
    parser.add_argument('--index', required=True, default='', help='index path')
    parser.add_argument('--hits', default=10, help='number of hits to retrieve')
    parser.add_argument('--k1', default=0.82, help='BM25 k1 parameter')
    parser.add_argument('--b', default=0.68, help='BM25 b parameter')
    # See our MS MARCO documentation to understand how these parameter values were tuned.
    parser.add_argument('--rm3', action='store_true', default=False, help='use RM3')
    parser.add_argument('--fbTerms', default=10, type=int, help='RM3 parameter: number of expansion terms')
    parser.add_argument('--fbDocs', default=10, type=int, help='RM3 parameter: number of documents')
    parser.add_argument('--originalQueryWeight', default=0.5, type=float, help='RM3 parameter: weight to assign to the original query')
    parser.add_argument('--threads', default=1, type=int, help='Maximum number of threads')

    args = parser.parse_args()

    total_start_time = time.time()

    searcher = pysearch.SimpleSearcher(args.index)
    searcher.set_bm25_similarity(float(args.k1), float(args.b))
    print('Initializing BM25, setting k1={} and b={}'.format(args.k1, args.b), flush=True)
    if args.rm3:
        searcher.set_rm3_reranker(args.fbTerms, args.fbDocs, args.originalQueryWeight)
        print('Initializing RM3, setting fbTerms={}, fbDocs={} and originalQueryWeight={}'.format(args.fbTerms, args.fbDocs, args.originalQueryWeight), flush=True)

    if args.threads == 1:

        with open(args.output, 'w') as fout:
            start_time = time.time()
            for line_number, line in enumerate(open(args.qid_queries, 'r', encoding='utf8')):
                qid, query = line.strip().split('\t')
                hits = searcher.search(query.encode('utf8'), int(args.hits))
                if line_number % 100 == 0:
                    time_per_query = (time.time() - start_time) / (line_number + 1)
                    print('Retrieving query {} ({:0.3f} s/query)'.format(line_number, time_per_query), flush=True)
                for rank in range(len(hits)):
                    docno = hits[rank].docid
                    fout.write('{}\t{}\t{}\n'.format(qid, docno, rank + 1))
    else:
        qids = []
        queries = []
        result_dict = {}

        for line_number, line in enumerate(open(args.qid_queries)):
            qid, query = line.strip().split('\t')
            qids.append(qid)
            queries.append(query)

        results = searcher.batch_search(queries, qids, args.hits, -1, args.threads)

        with open(args.output, 'w') as fout:
            for qid in qids:
                hits = results.get(qid)
                for rank in range(len(hits)):
                    docno = hits[rank].docid
                    fout.write('{}\t{}\t{}\n'.format(qid, docno, rank + 1))

    total_time = (time.time() - total_start_time)
    print('Total retrieval time: {:0.3f} s'.format(total_time))
    print('Done!')

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

# Pyjnius setup
anserini_root='.'
import sys
sys.path += ['src/main/python']
from pyserini.setup import configure_classpath
configure_classpath(anserini_root)

from jnius import autoclass
JString = autoclass('java.lang.String')
JSearcher = autoclass('io.anserini.search.SimpleSearcher')

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Retrieve Open Research Passages.')
    parser.add_argument('--qid_queries', required=True, default='', help='query id - query mapping file')
    parser.add_argument('--valid_docs', default='', help='valid doc ids file')
    parser.add_argument('--output', required=True, default='', help='output filee')
    parser.add_argument('--index', required=True, default='', help='index path')
    parser.add_argument('--hits', default=10, type=int, help='number of hits to retrieve')
    parser.add_argument('--k1', default=1.2, type=float, help='BM25 k1 parameter')
    parser.add_argument('--b', default=0.75, type=float, help='BM25 b parameter')
    # See our MS MARCO documentation to understand how these parameter values were tuned.
    parser.add_argument('--rm3', action='store_true', default=False, help='use RM3')
    parser.add_argument('--fbTerms', default=10, type=int, help='RM3 parameter: number of expansion terms')
    parser.add_argument('--fbDocs', default=10, type=int, help='RM3 parameter: number of documents')
    parser.add_argument('--originalQueryWeight', default=0.5, type=float, help='RM3 parameter: weight to assign to the original query')

    args = parser.parse_args()

    data_type = 'oc'
    if args.valid_docs:
      data_type = 'pd'
      valid_docs = set(open(args.valid_docs).read().strip().split('\n'))

    searcher = JSearcher(JString(args.index))
    searcher.setBM25Similarity(args.k1, args.b)
    print('Initializing BM25, setting k1={} and b={}'.format(args.k1, args.b))
    if args.rm3:
        searcher.setRM3Reranker(args.fbTerms, args.fbDocs, args.originalQueryWeight)
        print('Initializing RM3, setting fbTerms={}, fbDocs={} and originalQueryWeight={}'.format(args.fbTerms, args.fbDocs, args.originalQueryWeight))

    with open(args.output, 'w') as fout:
      start_time = time.time()
      for line_number, line in enumerate(open(args.qid_queries)):
          query_id, query = line.strip().split('\t')
          # We return one more result because it is almost certain that we will 
          # retrieve the document that originated the query.
          hits = searcher.search(
              JString(query.encode('utf8')), args.hits + 1)

          if line_number % 10 == 0:
              time_per_query = (time.time() - start_time) / (line_number + 1)
              print('Retrieving query {} ({:0.3f} s/query)'.format(
                  line_number, time_per_query))
          rank = 0
          for i in range(len(hits)):
              doc_id = hits[i].docid
              # We skip the doc that originated the query, we also skipped the doc that doesn't in valid docs.
              if data_type == 'oc' and doc_id == query_id or data_type == 'pd' and (doc_id == query_id or doc_id not in valid_docs):
                    continue
              fout.write('{} Q0 {} {} {} Anserini\n'.format(
                  query_id, doc_id, rank + 1, hits[i].score))
              rank += 1
              if rank >= args.hits:
                  break

    print('Done!')

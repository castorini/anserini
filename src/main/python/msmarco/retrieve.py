# -*- coding: utf-8 -*-
"""
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
"""

import argparse
import time

import jnius_config
jnius_config.set_classpath("target/anserini-0.4.1-SNAPSHOT-fatjar.jar")

from jnius import autoclass
JString = autoclass('java.lang.String')
JSearcher = autoclass('io.anserini.search.SimpleSearcher')

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Retrieve MS MARCO Passages')
    parser.add_argument('--qid_queries', required=True, default='', help='query id - query mapping file')
    parser.add_argument('--output', required=True, default='', help='output filee')
    parser.add_argument('--index', required=True, default='', help='index path')
    parser.add_argument('--hits', default=10, help='number of hits to retrieve')
    parser.add_argument('--b1', default=0.9, help='BM25 b1 parameter')
    parser.add_argument('--k', default=0.4, help='BM25 k parameter')
    args = parser.parse_args()

    searcher = JSearcher(JString(args.index))
    searcher.setBM25Similarity(args.b1, args.k)
    print('Initializing BM25, setting b1={} and k={}'.format(args.b1, args.k))

    with open(args.output, 'w') as fout:
      start_time = time.time()
      for line_number, line in enumerate(open(args.qid_queries)):
          qid, query = line.strip().split('\t')
          hits = searcher.search(JString(query.encode('utf8')), int(args.hits))
          if line_number % 10 == 0:
              time_per_query = (time.time() - start_time) / (line_number + 1)
              print('Retrieving query {} ({:0.3f} s/query)'.format(line_number, time_per_query))
          for rank in range(len(hits)):
              docno = hits[rank].docid
              fout.write('{}\t{}\t{}\n'.format(qid, docno, rank + 1))

    print('Done!')

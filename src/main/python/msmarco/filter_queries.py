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

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Keeps only queries that are in the qrels file.')
    parser.add_argument('--qrels', required=True, help='MS MARCO .tsv qrels file')
    parser.add_argument('--queries', required=True, help='queries file')
    parser.add_argument('--output_queries', required=True, help='path to write the queries file')

    args = parser.parse_args()

    qrels = set()
    with open(args.qrels) as f:
        for line in f:
            query_id, _, _, _ = line.rstrip().split('\t')
            qrels.add(query_id)

    with open(args.output_queries, 'w', encoding='utf-8', newline='\n') as fout:
        with open(args.queries, encoding='utf-8') as f:
            for line in f:
                query_id, _ = line.rstrip().split('\t')
                if query_id in qrels:
                    fout.write(line)

    print('Done!')

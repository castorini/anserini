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

import json
import argparse

def generate_queries_and_qrels(args):
    queries = {}
    with open(args.dataset_file, 'r', encoding='utf-8') as f_in:
        # open qrels file if provided
        if args.output_qrels_file:
            print('Generating qrels...')
            qrels_out = open(args.output_qrels_file, 'w', encoding='utf-8')

        for line in f_in:
            line_json = json.loads(line.strip())
            qid = line_json['id']
            query = line_json['claim']

            # save query to queries dict
            queries[qid] = query

            if 'label' in line_json:  # no "label" field in test datasets
                label = line_json['label']
                if label == 'NOT ENOUGH INFO':
                    continue

                # write claims and evidence to qrels file if provided
                if args.output_qrels_file:
                    # dedupe evidences for the query
                    evidences = set()
                    for annotator in line_json['evidence']:
                        for evidence in annotator:
                            if args.granularity == 'sentence':
                                evidences.add((evidence[2], evidence[3]))
                            else:  # args.granularity == 'paragraph'
                                evidences.add(evidence[2])

                    # write deduped evidences to qrels file
                    if args.granularity == 'sentence':
                        for doc_id, sentence_id in evidences:
                            qrels_out.write(f'{qid}\t0\t{doc_id}_{sentence_id}\t2\n')
                    else:  # args.granularity == 'paragraph'
                        for doc_id in evidences:
                            qrels_out.write(f'{qid}\t0\t{doc_id}\t2\n')

        # close qrels file if provided
        if args.output_qrels_file:
            qrels_out.close()

    # write queries to queries file if provided
    if args.output_queries_file:
        print('Generating queries...')
        with open(args.output_queries_file, 'w', encoding='utf-8') as f_out:
            for qid, query in queries.items():
                f_out.write(f'{qid}\t{query}\n')

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Generates queries and qrels files from a FEVER dataset file.')
    parser.add_argument('--dataset_file', required=True, help='FEVER dataset file.')
    parser.add_argument('--output_queries_file', help='Output queries file.')
    parser.add_argument('--output_qrels_file', help='Output qrels file.')
    parser.add_argument('--granularity',
                        required=True,
                        choices=['paragraph', 'sentence'],
                        help='The granularity of the source documents to index. Either "paragraph" or "sentence".')
    args = parser.parse_args()

    if not args.output_queries_file and not args.output_qrels_file:
        print('Please provide at least one of --output_queries_file or --output_qrels_file.')
        exit()

    generate_queries_and_qrels(args)

    print('Done!')

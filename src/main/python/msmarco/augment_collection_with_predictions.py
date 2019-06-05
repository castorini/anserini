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

import json
import os
import argparse

def convert_collection(args):
    print('Converting collection...')

    predictions_file = open(args.predictions)
    file_index = 0
    with open(args.collection_path) as f:
        for i, line in enumerate(f):
            # Start writting to a new file whent the current one reached its maximum capacity. 
            if i % args.max_docs_per_file == 0:
                if i > 0:
                    output_jsonl_file.close()
                output_path = os.path.join(args.output_folder, 'docs{:02d}.json'.format(file_index))
                output_jsonl_file = open(output_path, 'w')
                file_index += 1

            doc_id, doc_text = line.rstrip().split('\t')

            # Reads from predictions and merge then to the original doc text. 
            pred_text = []
            for _ in range(args.stride):
                pred_text.append(predictions_file.readline().strip())
            pred_text = ' '.join(pred_text)
            pred_text = pred_text.replace(' / ', ' ')
            text = (doc_text + ' ') * args.original_copies + pred_text
      
            output_dict = {'id': doc_id, 'contents': text}
            output_jsonl_file.write(json.dumps(output_dict) + '\n')

            if i % 100000 == 0:
                print('Converted {} docs in {} files'.format(i, file_index))

    output_jsonl_file.close()
    predictions_file.close()


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Augments MS MARCO tsv collection with predicted queries to create Anserini jsonl collection')
    parser.add_argument('--collection_path', required=True, help='MS MARCO tsv collection')
    parser.add_argument('--predictions', required=True, help='query predictions file')
    parser.add_argument('--output_folder', required=True, help='output folder for jsonl collection')
    parser.add_argument('--stride', required=True, type=int, help='even [s] lines in predictions file is associated with each document')
    parser.add_argument('--max_docs_per_file', default=1000000, type=int, help='maximum number of documents in each jsonl file')
    parser.add_argument('--original_copies', default=1, type=int, help='number of copies of the original document to duplicate')

    args = parser.parse_args()

    if not os.path.exists(args.output_folder):
        os.makedirs(args.output_folder)

    convert_collection(args)
    print('Done!')

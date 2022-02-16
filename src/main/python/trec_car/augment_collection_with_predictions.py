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
import os
import argparse
from trec_car_classes import *


def convert_collection(args):
    print('Converting collection...')

    predictions_file = open(args.predictions)
    file_index = 0
    with open(args.collection_path, 'rb') as f:

        for i, para_obj in enumerate(iter_paragraphs(f)):

            # Start writting to a new file whent the current one reached its maximum capacity. 
            if i % args.max_docs_per_file == 0:
                if i > 0:
                    output_jsonl_file.close()
                output_path = os.path.join(args.output_folder, 'docs{:02d}.json'.format(file_index))
                output_jsonl_file = open(output_path, 'w')
                file_index += 1

            doc_id = para_obj.para_id
            para_txt = [elem.text if isinstance(elem, ParaText)
                        else elem.anchor_text
                        for elem in para_obj.bodies]

            doc_text = ' '.join(para_txt)
            doc_text = doc_text.replace('\n', ' ')
            doc_text = ' '.join(doc_text.split())

            if not doc_text:
                doc_text = 'dummy document.'

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
    parser = argparse.ArgumentParser(
        description='Augments TREC CAR collection with predicted queries ' +
                    'to create an expanded Anserini jsonl collection.')
    parser.add_argument('--collection-path', required=True, help='TREC CAR cbor collection.')
    parser.add_argument('--predictions', required=True, help='Query predictions file.')
    parser.add_argument('--output-folder', required=True, help='Qutput folder for jsonl collection.')
    parser.add_argument('--stride', required=True, type=int,
                        help='Every [s] lines in predictions file is associated with each document.')
    parser.add_argument('--max-docs-per-file', default=1000000, type=int,
                        help='Maximum number of documents in each jsonl file.')
    parser.add_argument('--original-copies', default=1, type=int,
                        help='Number of copies of the original document to duplicate.')
    args = parser.parse_args()

    if not os.path.exists(args.output_folder):
        os.makedirs(args.output_folder)

    convert_collection(args)
    print('Done!')

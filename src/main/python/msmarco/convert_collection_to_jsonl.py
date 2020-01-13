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
    file_index = 0

    # open as bytes to decode from UTF-8 and apply proper encodings
    with open(args.collection_path, 'rb') as f:
        for i, line in enumerate(l.decode('utf-8') for l in f):
            doc_id, doc_text = line.rstrip().split('\t')

            # it seems like MSMARCO data was originally encoded as latin-1 and then
            # wrongly encoded as UTF-8 although there is no mention of this anywhere
            try:
                doc_text = doc_text.encode("latin-1").decode('utf-8')
            # skip documents with encodings that cause errors when decoding back to utf-8
            # this was a very small number for MSMARCO passage (~45 documents)
            except:
                pass

            if i % args.max_docs_per_file == 0:
                if i > 0:
                    output_jsonl_file.close()
                output_path = os.path.join(args.output_folder, 'docs{:02d}.json'.format(file_index))
                output_jsonl_file = open(output_path, 'w', encoding='utf-8')
                file_index += 1
            output_dict = {'id': doc_id, 'contents': doc_text}

            # set ensure_ascii=False to ensure unicode characters are directly stored
            output_str = json.dumps(output_dict, ensure_ascii=False)
            output_jsonl_file.write(output_str)

            if i % 100000 == 0:
                print('Converted {} docs in {} files'.format(i, file_index))

    output_jsonl_file.close()

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='''Converts MSMARCO's tsv collection to Anserini jsonl files.''')
    parser.add_argument('--collection_path', required=True, help='MS MARCO .tsv collection file')
    parser.add_argument('--output_folder', required=True, help='output filee')
    parser.add_argument('--max_docs_per_file', default=1000000, type=int, help='maximum number of documents in each jsonl file.')

    args = parser.parse_args()

    if not os.path.exists(args.output_folder):
        os.makedirs(args.output_folder)

    convert_collection(args)
    print('Done!')

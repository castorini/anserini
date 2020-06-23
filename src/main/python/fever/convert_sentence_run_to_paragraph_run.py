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

def convert_run(args):
    with open(args.input_run_file, 'r', encoding='utf-8') as f_in, \
            open(args.output_run_file, 'w', encoding='utf-8') as f_out:
        curr_query = -1
        curr_docs = set()
        curr_rank = 1
        for line in f_in:
            query_id, sent_id, rank = line.strip().split('\t')

            # if we reach a new query in the run file, reset curr_* variables
            if query_id != curr_query:
                curr_query = query_id
                curr_docs.clear()
                curr_rank = 1

            doc_id = sent_id[:sent_id.rfind('_')]  # cut off appended sent_id to get doc_id
            if doc_id not in curr_docs:
                curr_docs.add(doc_id)
                f_out.write(f'{query_id}\t{doc_id}\t{curr_rank}\n')
                curr_rank += 1

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Converts a run file from a sentence to a paragraph granularity.')
    parser.add_argument('--input_run_file', required=True, help='Anserini run file based on sentence retrieval.')
    parser.add_argument('--output_run_file', required=True, help='Anserini run file based on paragraph retrieval.')
    args = parser.parse_args()

    convert_run(args)

    print('Done!')

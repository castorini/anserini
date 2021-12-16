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
import logging


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='Converts a TREC run file to a MS MARCO-formatted run file.')
    parser.add_argument('--input', required=True, default='', help='TREC-formatted run file')
    parser.add_argument('--output', required=True, default='',
                        help='output MS MARCO-formatted run file')
    parser.add_argument('--k', type=int, default=-1,
                        help='Number of hits to write to the run file. Write all hits if -1.')
    parser.add_argument('--quiet', action='store_true', help="Suppresses all warnings.")

    args = parser.parse_args()

    with open(args.output, 'w') as fout:
        last_score = None
        last_query_id = ''
        last_doc_id = ''
        n_docs = 0
        for line in open(args.input):
            query_id, _, doc_id, rank, score, _ = line.strip().split(' ')
            rank = int(rank)
            score = float(score)
            if query_id != last_query_id:
                last_score = None
                n_docs = 0

            if last_score is not None:
                if score == last_score and not args.quiet:
                    logging.warning(
                        f'Score of {score} for doc id {doc_id} is the same of doc id '
                        f'{last_doc_id} for query id {query_id}. This will likely impact metrics '
                        ' negatively.')

                if rank == last_rank and not args.quiet:
                    logging.warning(
                        f'Rank of {rank} for doc id {doc_id} is the same of doc id '
                        f'{last_doc_id} for query id {query_id}. This will likely impact metrics '
                        ' negatively.')

                if score > last_score and not args.quiet:
                    logging.warning(
                        f'Score of {score} for current doc id {doc_id} is greater than the score '
                        f'{last_score} of the previous doc id {last_doc_id} for query id '
                        f'{query_id}. This will likely impact metrics negatively.')

                if rank < last_rank and not args.quiet:
                    logging.warning(
                        f'Rank of {rank} for current doc id {doc_id} is lower than the rank '
                        f'{last_rank} of the previous doc id {last_doc_id} for query id '
                        f'{query_id}. This will likely impact metrics negatively.')

            if args.k == -1 or n_docs < args.k:
                fout.write('{}\t{}\t{}\n'.format(query_id, doc_id, rank))

            last_query_id = query_id
            last_doc_id = doc_id
            last_score = score
            last_rank = rank
            n_docs += 1

    logging.info(f'Done! Wrote output file to {args.output}')

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


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Convert MS MARCO qrels to TREC qrels.')
    parser.add_argument('--input', required=True, default='', help='Input MS MARCO qrels file.')
    parser.add_argument('--output', required=True, default='', help='Output TREC qrels file.')

    args = parser.parse_args()

    with open(args.output, 'w') as fout:
        for line in open(args.input):
            fout.write(line.replace('\t', ' '))

    print('Done!')

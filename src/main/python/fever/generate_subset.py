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
import json

def generate_subset(args):
    num_supports = 0
    num_refutes = 0
    num_nei = 0

    with open(args.dataset_file, 'r', encoding='utf-8') as f_in, open(args.subset_file, 'w', encoding='utf-8') as f_out:
        for line in f_in:
            # stop looping if we have everything needed in subset
            if num_supports >= args.length // 3 and num_refutes >= args.length // 3 and num_nei >= args.length // 3:
                break

            line_json = json.loads(line.strip())
            if line_json['label'] == 'SUPPORTS':
                # skip this loop iteration if we have enough "SUPPORTS" samples
                if num_supports >= args.length // 3:
                    continue
                num_supports += 1
            elif line_json['label'] == 'REFUTES':
                # skip this loop iteration if we have enough "REFUTES" samples
                if num_refutes >= args.length // 3:
                    continue
                num_refutes += 1
            else:  # line_json['label'] == 'NOT ENOUGH INFO'
                # skip this loop iteration if we have enough "NOT ENOUGH INFO" samples
                if num_nei >= args.length // 3:
                    continue
                num_nei += 1
            f_out.write(line)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Generates a subset of a FEVER dataset file.')
    parser.add_argument('--dataset_file', required=True, help='FEVER dataset file.')
    parser.add_argument('--subset_file', required=True, help='Output subset of dataset file.')
    parser.add_argument('--length', default=10000, type=int, help='Number of lines in final subset file.')
    args = parser.parse_args()

    generate_subset(args)

    print('Done!')

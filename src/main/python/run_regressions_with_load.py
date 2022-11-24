#
# Anserini: A Lucene toolkit for reproducible information retrieval research
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
import os
import time


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='Run regressions in parallel while maintaining a target load threshold.')
    parser.add_argument('--file', type=str, default=None, help="File with regression commands.")
    parser.add_argument('--sleep', type=int, default=30, help="minimum idf")
    parser.add_argument('--load', type=int, default=10, help="minimum idf")
    args = parser.parse_args()

    print(f'Running commands in {args.file}')
    print(f'Sleep interval: {args.sleep}')
    print(f'Threshold load: {args.load}')

    with open(args.file) as f:
        lines = f.read().splitlines()

    for r in lines:
        print(r)
        os.system(r + ' &')

        while True:
            time.sleep(5)
            load = os.getloadavg()[0]
            print(f'Current load: {load}')
            if load < args.load:
                break

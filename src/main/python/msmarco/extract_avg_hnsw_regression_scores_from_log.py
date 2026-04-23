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

import re
import subprocess


beir_keys = [
    'dl19-passage-cohere-embed-english-v3-hnsw',
    'dl19-passage-cohere-embed-english-v3-hnsw-int8',
    'dl20-passage-cohere-embed-english-v3-hnsw',
    'dl20-passage-cohere-embed-english-v3-hnsw-int8',
    'msmarco-passage-cohere-embed-english-v3-hnsw',
    'msmarco-passage-cohere-embed-english-v3-hnsw-int8'
]

for key in sorted(beir_keys):
    print(key)
    for metric in ['AP@1000', 'nDCG@10', 'RR@10', 'R@100', 'R@1000']:
        command = f'tail -n 5 logs/log.{key}_* | grep "{metric}\s"'
        p = subprocess.run(command, shell=True, text=True, capture_output=True)
        output = p.stdout
        scores = []
        for line in output.rstrip().split('\n'):
            pattern = r'actual: (\d.\d\d\d)'
            match = re.search(pattern, line)
            if match:
                scores.append(float(match.group(1)))
        if len(scores) > 0:
            avg = round(sum(scores)/len(scores) * 10 ** 3) / (10 ** 3)
            print(f'      {metric} (avg over {len(scores)}):\n        - {avg:.3f}')

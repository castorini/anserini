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

import yaml

from collections import defaultdict


lang = [
    ['ar', 'arabic'],
    ['bn', 'bengali'],
    ['en', 'english'],
    ['fi', 'finnish'],
    ['id', 'indonesian'],
    ['ja', 'japanese'],
    ['ko', 'korean'],
    ['ru', 'russian'],
    ['sw', 'swahili'],
    ['te', 'telugu'],
    ['th', 'thai']
]

metrics = ['MRR@100', 'R@100']

table = defaultdict(lambda: defaultdict(lambda: defaultdict(lambda: 0.0)))
top_level_sums = defaultdict(lambda: defaultdict(float))
cqadupstack_sums = defaultdict(lambda: defaultdict(float))
final_scores = defaultdict(lambda: defaultdict(float))

for key in lang:
    yaml_file = f'src/main/resources/regression/mrtydi-v1.1-{key[0]}.yaml'
    with open(yaml_file) as f:
        yaml_data = yaml.safe_load(f)
        for metric in metrics:
            table[key[0]]['train'][metric] = float(yaml_data['models'][0]['results'][metric][0])
            table[key[0]]['dev'][metric] = float(yaml_data['models'][0]['results'][metric][1])
            table[key[0]]['test'][metric] = float(yaml_data['models'][0]['results'][metric][2])

print(f'Train       MRR@100  R@100')
print(f'----------- ------- ------')
for key in lang:
    print(f'{key[1]:12} {table[key[0]]["train"]["MRR@100"]:.4f} {table[key[0]]["train"]["R@100"]:.4f}')
print('')

print(f'Dev         MRR@100  R@100')
print(f'----------- ------- ------')
for key in lang:
    print(f'{key[1]:12} {table[key[0]]["dev"]["MRR@100"]:.4f} {table[key[0]]["dev"]["R@100"]:.4f}')
print('')

print(f'Test        MRR@100  R@100')
print(f'----------- ------- ------')
for key in lang:
    print(f'{key[1]:12} {table[key[0]]["test"]["MRR@100"]:.4f} {table[key[0]]["test"]["R@100"]:.4f}')

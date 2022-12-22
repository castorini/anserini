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


beir_keys = ['trec-covid',
             'bioasq',
             'nfcorpus',
             'nq',
             'hotpotqa',
             'fiqa',
             'signal1m',
             'trec-news',
             'robust04',
             'arguana',
             'webis-touche2020',
             'cqadupstack-android',
             'cqadupstack-english',
             'cqadupstack-gaming',
             'cqadupstack-gis',
             'cqadupstack-mathematica',
             'cqadupstack-physics',
             'cqadupstack-programmers',
             'cqadupstack-stats',
             'cqadupstack-tex',
             'cqadupstack-unix',
             'cqadupstack-webmasters',
             'cqadupstack-wordpress',
             'quora',
             'dbpedia-entity',
             'scidocs',
             'fever',
             'climate-fever',
             'scifact'
             ]

models = ['flat', 'multifield', 'unicoil-noexp', 'splade-distil-cocodenser-medium']
metrics = ['nDCG@10', 'R@100', 'R@1000']

table = defaultdict(lambda: defaultdict(lambda: defaultdict(lambda: 0.0)))
top_level_sums = defaultdict(lambda: defaultdict(float))
cqadupstack_sums = defaultdict(lambda: defaultdict(float))
final_scores = defaultdict(lambda: defaultdict(float))

for key in beir_keys:
    for model in models:
        yaml_file = f'src/main/resources/regression/beir-v1.0.0-{key}-{model}.yaml'
        with open(yaml_file) as f:
            yaml_data = yaml.safe_load(f)
            for metric in metrics:
                table[key][model][metric] = float(yaml_data['models'][0]['results'][metric][0])

# Compute the running sums to compute the final mean scores
for key in beir_keys:
    for model in models:
        for metric in metrics:
            if key.startswith('cqa'):
                # The running sum for cqa needs to be kept separately.
                cqadupstack_sums[model][metric] += table[key][model][metric]
            else:
                top_level_sums[model][metric] += table[key][model][metric]

# Compute the final mean
for model in models:
    for metric in metrics:
        # Compute mean over cqa sub-collections first
        cqa_score = cqadupstack_sums[model][metric] / 12
        # Roll cqa scores into final overall mean
        final_score = (top_level_sums[model][metric] + cqa_score) / 18
        final_scores[model][metric] = final_score

for metric in metrics:
    print(f'{metric:25}flat    multi   UCx     SPLADE')
    print(' ' * 25 + '-' * 6 + '  ' + '-' * 6 + '  ' + '-' * 6 + '  ' + '-' * 6)
    for key in beir_keys:
        print(f'{key:25}{table[key]["flat"][metric]:.4f}  ' +
              f'{table[key]["multifield"][metric]:.4f}  ' +
              f'{table[key]["unicoil-noexp"][metric]:.4f}  ' +
              f'{table[key]["splade-distil-cocodenser-medium"][metric]:.4f}')

    print(' ' * 25 + '-' * 6 + '  ' + '-' * 6 + '  ' + '-' * 6 + '  ' + '-' * 6)
    print(' ' * 25 + f'{final_scores["flat"][metric]:0.4f}  ' +
          f'{final_scores["multifield"][metric]:0.4f}  ' +
          f'{final_scores["unicoil-noexp"][metric]:0.4f}  ' +
          f'{final_scores["splade-distil-cocodenser-medium"][metric]:0.4f}')
    print('\n')

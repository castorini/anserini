# -*- coding: utf-8 -*-
#
# Anserini: A toolkit for reproducible information retrieval research built on Lucene
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

from __future__ import print_function
import os, sys
import csv
import math
import logging
from operator import itemgetter
import matplotlib

# https://stackoverflow.com/questions/37604289/tkinter-tclerror-no-display-name-and-no-display-environment-variable
if os.environ.get('DISPLAY','') == '':
    print('no display found. Using non-interactive Agg backend')
    matplotlib.use('Agg')

import matplotlib.pyplot as plt

plt.style.use('ggplot')

logging.basicConfig()

class Plots(object):
    def __init__(self):
        self.logger = logging.getLogger('plot.Plots')
        self.run_files_root = 'run_files'
        self.eval_files_root = 'eval_files'
        self.effectiveness_root = 'effectiveness_files'
        self.plots_root = 'plots'

        self.title_mappings = {
            'disk12': 'Disk 1 & 2',
            'robust04': 'Disks 4 & 5',
            'robust05': 'AQUAINT',
            'core17': 'New York Times',
            'core18': 'Washington Post',
            'wt10g': 'WT10g',
            'gov2': 'Gov2',
            'cw09b': 'ClueWeb09b',
            'cw12b13': 'ClueWeb12-B13',
            'cw12': 'ClueWeb12',
            'mb11': 'Tweets 2011',
            'mb13': 'Tweets 2013'
        }

    def read_data(self, fn):
        all_results = {}
        with open(fn) as f:
            r = csv.reader(f)
            for row in r:
                model, para, score = row
                if model not in all_results:
                    all_results[model] = []
                all_results[model].append((float(para), float(score)))
        return all_results

    def plot_params_sensitivity(self, collection, output_root):
        if not os.path.exists(os.path.join(output_root, self.plots_root)):
            os.makedirs(os.path.join(output_root, self.plots_root))

        for fn in os.listdir(os.path.join(output_root, self.effectiveness_root)):
            if not fn.startswith('axiom_paras_sensitivity_'):
                continue
            all_results = self.read_data(os.path.join(output_root, self.effectiveness_root, fn))
            ls = ['-', '--', ':']
            colors = ['r', 'g', 'b']
            fig, ax = plt.subplots(1, 1, figsize=(6, 4))
            for (model, linestyle, color) in zip(sorted(all_results), ls, colors):
                all_results[model].sort(key = itemgetter(0))
                x = [float(ele[0]) for ele in all_results[model] if ele[0] > 0]
                y = [float(ele[1]) for ele in all_results[model] if ele[0] > 0]
                ax.plot(x, y, linestyle=linestyle, marker='o', ms=5, label=model.upper()+'+Ax', color=color)
                baseline = [float(ele[1]) for ele in all_results[model] if ele[0] < 0]
                if len(baseline) == 1:
                    ax.axhline(baseline[0], linestyle=linestyle, color=color, label=model.upper())
                ax.grid(True)
                ax.set_title(collection if collection not in self.title_mappings else self.title_mappings[collection])
                ax.set_xlabel(r'$\beta$')
                ax.set_ylabel('MAP' if not 'cw' in collection else 'NDCG@20')
                ax.legend(loc=4)
            output_fn = os.path.join(output_root, self.plots_root, 'params_sensitivity_{}.eps'.format(collection))
            plt.savefig(output_fn, bbox_inches='tight', format='eps')

    def plot_random_seeds(self, collection, output_root, beta):
        if not os.path.exists(os.path.join(output_root, self.plots_root)):
            os.makedirs(os.path.join(output_root, self.plots_root))

        for fn in os.listdir(os.path.join(output_root, self.effectiveness_root)):
            if not fn.startswith('axiom_random_seeds_'):
                continue
            fig, ax = plt.subplots(1, 1, figsize=(3, 3))
            random_seeds_results = self.read_data(os.path.join(output_root, self.effectiveness_root, fn))
            paras_results = self.read_data(os.path.join(output_root, self.effectiveness_root, fn.replace('axiom_random_seeds_', 'axiom_paras_sensitivity_')))
            for model in sorted(random_seeds_results):
                random_seeds_results[model].sort(key = itemgetter(0))
                y = [float(ele[1]) for ele in random_seeds_results[model]]
                ax.set_title(collection if collection not in self.title_mappings else self.title_mappings[collection])
                baseline = [float(ele[1]) for ele in paras_results[model] if ele[0] < 0]
                if len(baseline) == 1:
                    ax.axhline(y = baseline[0], color='b', linestyle='--', label='BM25')
                ax_baseline = [float(ele[1]) for ele in paras_results[model] if math.fabs(ele[0]-beta) < 1e-3]
                if len(ax_baseline) == 1:
                    ax.axhline(y = ax_baseline[0], color='g', linestyle=':', label='BM25+Ax')
                ax.boxplot(y, showfliers=True)
                ax.set_xticks([])
                #ax.legend(loc=0) # Legend is muted -- the explanations are in the text (caption)
            output_fn = os.path.join(output_root, self.plots_root, 'random_seeds_{}.eps'.format(collection))
            plt.savefig(output_fn, bbox_inches='tight', format='eps')

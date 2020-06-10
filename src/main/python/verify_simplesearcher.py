# -*- coding: utf-8 -*-
'''
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
'''

import logging
import regression_utils

logger = logging.getLogger('run_es_regression')
ch = logging.StreamHandler()
ch.setFormatter(logging.Formatter('%(asctime)s %(levelname)s - %(message)s'))
logger.addHandler(ch)
logger.setLevel(logging.INFO)

sc_command = 'target/appassembler/bin/SearchCollection   '
ss_command = 'target/appassembler/bin/SimpleSearcher     '
st_command = 'target/appassembler/bin/SimpleTweetSearcher'
robust04_index = 'indexes/lucene-index.robust04.pos+docvectors+raw'
robust04_topics = 'src/main/resources/topics-and-qrels/topics.robust04.txt'
mb11_index = 'indexes/lucene-index.mb11.pos+docvectors+raw'
mb11_topics = 'src/main/resources/topics-and-qrels/topics.microblog2011.txt'

# Raw data for verification: array of arrays
# Each top level array contains commands whose outputs should be *identical*
# Note that spacing is intentional to make command easy to read.
groups = [ \
           # Does not explicitly set similarity in SimpleSearcher, defaults to -bm25, default parameters, tests threading.
           [ f'{sc_command} -index {robust04_index} -topicreader Trec -topics {robust04_topics} -bm25            -output', \
             f'{ss_command} -index {robust04_index}                   -topics {robust04_topics}                  -output', \
             f'{ss_command} -index {robust04_index}                   -topics {robust04_topics}       -threads 4 -output' ], \
           [ f'{sc_command} -index {robust04_index} -topicreader Trec -topics {robust04_topics} -bm25 -rm3            -output', \
             f'{ss_command} -index {robust04_index}                   -topics {robust04_topics}       -rm3            -output', \
             f'{ss_command} -index {robust04_index}                   -topics {robust04_topics}       -rm3 -threads 4 -output' ], \
           # Explicitly set -bm25, which takes different code path than omitting the arg.
           [ f'{sc_command} -index {robust04_index} -topicreader Trec -topics {robust04_topics} -bm25 -output', \
             f'{ss_command} -index {robust04_index}                   -topics {robust04_topics} -bm25 -output' ], \
           [ f'{sc_command} -index {robust04_index} -topicreader Trec -topics {robust04_topics} -bm25 -bm25.k1 0.8 -bm25.b 0.2 -output', \
             f'{ss_command} -index {robust04_index}                   -topics {robust04_topics} -bm25 -bm25.k1 0.8 -bm25.b 0.2 -output' ], \
           # Explicitly set -ql.
           [ f'{sc_command} -index {robust04_index} -topicreader Trec -topics {robust04_topics} -qld -output', \
             f'{ss_command} -index {robust04_index}                   -topics {robust04_topics} -qld -output' ], \
           [ f'{sc_command} -index {robust04_index} -topicreader Trec -topics {robust04_topics} -qld -qld.mu 100 -output', \
             f'{ss_command} -index {robust04_index}                   -topics {robust04_topics} -qld -qld.mu 100 -output' ], \
           # Explicitly set RM3 parameters.
           [ f'{sc_command} -index {robust04_index} -topicreader Trec -topics {robust04_topics} -bm25 -rm3 -rm3.fbTerms 15 -rm3.fbDocs 5 -rm3.originalQueryWeight 0.2 -output', \
             f'{ss_command} -index {robust04_index}                   -topics {robust04_topics} -bm25 -rm3 -rm3.fbTerms 15 -rm3.fbDocs 5 -rm3.originalQueryWeight 0.2 -output' ], \
           [ f'{sc_command} -index {robust04_index} -topicreader Trec -topics {robust04_topics} -qld -rm3 -rm3.fbTerms 15 -rm3.fbDocs 5 -rm3.originalQueryWeight 0.2 -output', \
             f'{ss_command} -index {robust04_index}                   -topics {robust04_topics} -qld -rm3 -rm3.fbTerms 15 -rm3.fbDocs 5 -rm3.originalQueryWeight 0.2 -output' ], \
           # Tests SimpleTweetSearcher.
           [ f'{sc_command} -index {mb11_index} -topicreader Microblog -topics {mb11_topics} -bm25 -searchtweets -output', \
             f'{st_command} -index {mb11_index}                        -topics {mb11_topics}                     -output'], \
           [ f'{sc_command} -index {mb11_index} -topicreader Microblog -topics {mb11_topics} -bm25 -rm3 -searchtweets -output', \
             f'{st_command} -index {mb11_index}                        -topics {mb11_topics}       -rm3               -output'], \
         ]

if __name__ == '__main__':
    group_cnt = 0
    for group in groups:
        print(f'# Verifying Group {group_cnt}')
        entry_cnt = 0
        group_runs = []
        for entry in group:
             run_file = f'runs/run.ss_verify.g{group_cnt}.e{entry_cnt}.txt'
             cmd = f'{entry} {run_file}'
             print(f'Running: {cmd}')
             regression_utils.run_shell_command(cmd, logger, echo=False)

             # Load in the run file.
             with open(run_file, 'r') as file:
               group_runs.append(file.read().replace('\n', ''))

             entry_cnt += 1

        # Check that all run files are identical.
        for i in range(len(group_runs)):
            if group_runs[0] != group_runs[i]:
                raise ValueError(f'Group {group_cnt}: Results are not identical!')

        print(f'# Group {group_cnt}: Results identical')
        group_cnt += 1

    print('All tests passed!')

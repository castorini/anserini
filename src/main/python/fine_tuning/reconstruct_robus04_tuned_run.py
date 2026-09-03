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

"""Script to reconstruct tuned BM25 + RM3 run.

Takes as arguments the folds and per-fold parameters to reconstruct the
tuned BM25 + RM3 run.
"""

import argparse
import json
import re
import shlex
import subprocess


def resolve_topics_path(topics):
    result = subprocess.run(
        ['bin/run.sh', 'io.anserini.cli.TopicsRegistry', '--metadata', topics],
        check=True,
        capture_output=True,
        text=True
    )
    return json.loads(result.stdout)['local_path']


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--folds", type=str, help='folds file', required=True)
    parser.add_argument("--params", type=str, help='params file', required=True)
    parser.add_argument("--output", type=str, help='output run file', required=True)

    args = parser.parse_args()
    folds_file = args.folds
    params_file = args.params

    index = 'disk45'
    topics_file = resolve_topics_path('robust04')

    # Load folds.
    with open(folds_file) as f:
        folds = json.load(f)

    # Load params.
    with open(params_file) as f:
        params = json.load(f)

    # Load topics.
    topics = []
    with open(topics_file, 'r') as f:
        for line in f:
            if '<top>' in line:
                topics.append(line)
            else:
                topics[-1] += line

    # Generate separate topics for each fold.
    for i in range(len(folds)):
        with open(f'topics.robust04.fold{i}', 'w') as out:
            for t in range(len(topics)):
                match = re.search(r'Number: (\d+)', topics[t], re.MULTILINE)
                if match and str(match.group(1)) in folds[i]:
                    out.write(topics[t])

    # Generate run for each fold using tuned parameters.
    folds_run_files = []
    for i in range(len(folds)):
        subprocess.run([
            'bin/run.sh', 'io.anserini.search.SearchCollection', '-topicReader', 'Trec', '-index', index,
            '-topics', f'topics.robust04.fold{i}', '-output', f'{args.output}.fold{i}', '-hits', '1000',
            *shlex.split(params[i])
        ], check=True)
        folds_run_files.append(f'{args.output}.fold{i}')

    # Concatenate all partial run files together.
    print('Concatenating the following files:')
    with open(args.output, 'w') as outfile:
        for fname in folds_run_files:
            print(f' - {fname}')
            with open(fname) as infile:
                outfile.write(infile.read())

    print(f'Finished writing {args.output}')

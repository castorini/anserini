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

from datetime import datetime
import re
import subprocess


def run_command(cmd):
    try:
        output = subprocess.check_output(cmd, shell=True, universal_newlines=True)
        return output
    except subprocess.CalledProcessError as e:
        print(f"Error executing the command: {e}")


def analyze_page(page):
    print(f'Analyzing {page}')
    output = run_command(f"grep 'Results reproduced by' {page}")
    lines = output.rstrip().split('\n')
    cnt = len(lines)
    print(f' + count: {cnt}')

    start_date_str = re.search(r'\d\d\d\d-\d\d-\d\d', lines[0]).group()
    end_date_str = re.search(r'\d\d\d\d-\d\d-\d\d', lines[-1]).group()

    print(f' + start: {start_date_str}')
    print(f' + end:   {end_date_str}')

    date_format = '%Y-%m-%d'
    start_date = datetime.strptime(start_date_str, date_format)
    end_date = datetime.strptime(end_date_str, date_format)

    time_difference = end_date - start_date
    print(f' + total: {time_difference.days} days')
    print(f' + ratel: {time_difference.days/cnt:0.2f}')
    print('')


analyze_page('docs/start-here.md')
analyze_page('docs/experiments-msmarco-passage.md')

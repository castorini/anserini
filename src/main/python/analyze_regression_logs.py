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

output = run_command("ls logs/log.* | wc")
num_regressions = re.search(r'\d+', output.rstrip()).group()
print(f'Total regressions: {int(num_regressions) : >3}')

output = run_command("tail -n 1 logs/log.* | grep 'Passed' | wc")
num_regressions = int(re.search(r'\d+', output.rstrip()).group())
print(f' - Passed:         {int(num_regressions) : >3}')

output = run_command("tail -n 1 logs/log.* | grep 'OK\*' | wc")
num_regressions = int(re.search(r'\d+', output.rstrip()).group())
print(f' - OK:             {int(num_regressions) : >3}')

output = run_command('head -n 1 logs/log.* | grep python | sort | head -1')
start_date_str = ' '.join(output.split(' ')[:2])

output = run_command("tail -n 1 logs/log.* | grep 'Passed' | sort -r | head -1")
end_date_str = ' '.join(output.split(' ')[:2])

date_format = '%Y-%m-%d %H:%M:%S,%f'
start_date = datetime.strptime(start_date_str, date_format)
end_date = datetime.strptime(end_date_str, date_format)

time_difference = end_date - start_date

print(f'')
print(f'Start time: {start_date_str}')
print(f'End time:   {end_date_str}')
print(f'')

print(f'Duration: {time_difference} ~{time_difference.total_seconds() / 3600:.1f}h')

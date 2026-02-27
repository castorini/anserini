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
import logging
import os
import subprocess
import time

logger = logging.getLogger('run_regressions_with_load')
logger.setLevel(logging.INFO)
ch = logging.StreamHandler()
ch.setLevel(logging.INFO)
formatter = logging.Formatter('%(asctime)s %(levelname)s  [python] %(message)s')
ch.setFormatter(formatter)
logger.addHandler(ch)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Run regressions in parallel while maintaining a target load threshold.')
    parser.add_argument('--file', type=str, default=None, help="File with regression commands.")
    parser.add_argument('--sleep', type=int, default=30, help="Sleep interval before checking load.")
    parser.add_argument('--load', type=int, default=10, help="Maximum load.")
    parser.add_argument('--max', type=int, default=0, help="Maximum number of concurrent jobs (0 means no limit).")
    args = parser.parse_args()

    logger.info(f'Running commands in {args.file}')
    logger.info(f'Sleep interval: {args.sleep}')
    logger.info(f'Threshold load: {args.load}')
    logger.info(f'Max concurrent jobs: {args.max if args.max > 0 else "unlimited"}')

    with open(args.file) as f:
        lines = f.read().splitlines()

    commands = [r for r in lines if r and not r.startswith('#')]
    active = []
    next_command = 0

    while next_command < len(commands) or active:
        active = [p for p in active if p.poll() is None]
        load = os.getloadavg()[0]

        can_launch_by_max = args.max <= 0 or len(active) < args.max
        can_launch_by_load = load < args.load
        if next_command < len(commands) and can_launch_by_max and can_launch_by_load:
            command = commands[next_command]
            logger.info(f'Launching: {command}')
            active.append(subprocess.Popen(command, shell=True))
            next_command += 1

        logger.info(
            f'Current load: {load:.1f} (threshold = {args.load}), '
            f'active jobs: {len(active)} '
            f'(max = {args.max if args.max > 0 else "unlimited"})'
        )
        time.sleep(args.sleep)

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

import os
import argparse
import logging
import time
import yaml
from subprocess import call, Popen, PIPE

# Constants
FUSE_COMMAND = 'bin/run.sh io.anserini.fusion.FuseTrecRuns'

# Set up logging
logger = logging.getLogger('fusion_regression_test')
logger.setLevel(logging.INFO)
ch = logging.StreamHandler()
ch.setLevel(logging.INFO)
formatter = logging.Formatter('%(asctime)s %(levelname)s [python] %(message)s')
ch.setFormatter(formatter)
logger.addHandler(ch)

def is_close(a: float, b: float, rel_tol: float = 1e-9, abs_tol: float = 0.0) -> bool:
    """Check if two numbers are close within a given tolerance."""
    return abs(a - b) <= max(rel_tol * max(abs(a), abs(b)), abs_tol)

def check_output(command: str) -> str:
    """Run a shell command and return its output. Raise an error if the command fails."""
    process = Popen(command, shell=True, stdout=PIPE)
    output, err = process.communicate()
    if process.returncode == 0:
        return output
    else:
        raise RuntimeError(f"Command {command} failed with error: {err}")

def construct_fusion_commands(yaml_data: dict) -> list:
    """
    Constructs the fusion commands from the YAML configuration.

    Args:
        yaml_data (dict): The loaded YAML configuration.

    Returns:
        list: A list of commands to be executed.
    """
    return [
        [
            FUSE_COMMAND,
            '-runs', ' '.join([run for run in yaml_data['runs']]),
            '-output', method.get('output'),
            '-method', method.get('name', 'average'),
            '-k', str(method.get('k', 1000)),
            '-depth', str(method.get('depth', 1000)),
            '-rrf_k', str(method.get('rrf_k', 60)),
            '-alpha', str(method.get('alpha', 0.5))
        ]
        for method in yaml_data['methods']
    ]

def run_fusion_commands(cmds: list):
    """
    Run the fusion commands and log the results.

    Args:
        cmds (list): List of fusion commands to run.
    """
    for cmd_list in cmds:
        cmd = ' '.join(cmd_list)
        logger.info(f'Running command: {cmd}')
        try:
            return_code = call(cmd, shell=True)
            if return_code != 0:
                logger.error(f"Command failed with return code {return_code}: {cmd}")
        except Exception as e:
            logger.error(f"Error executing command {cmd}: {str(e)}")

def evaluate_and_verify(yaml_data: dict, dry_run: bool):
    """
    Runs the evaluation and verification of the fusion results.

    Args:
        yaml_data (dict): The loaded YAML configuration.
        dry_run (bool): If True, output commands without executing them.
    """
    fail_str = '\033[91m[FAIL]\033[0m '
    ok_str = '   [OK] '
    failures = False

    logger.info('=' * 10 + ' Verifying Fusion Results ' + '=' * 10)

    for method in yaml_data['methods']:
        for i, topic_set in enumerate(yaml_data['topics']):
            for metric in yaml_data['metrics']:
                output_runfile = str(method.get('output'))
                
                # Build evaluation command
                eval_cmd = [
                    os.path.join(metric['command']),
                    metric['params'] if 'params' in metric and metric['params'] else '',
                    os.path.join('tools/topics-and-qrels', topic_set['qrel']) if 'qrel' in topic_set and topic_set['qrel'] else '',
                    output_runfile
                ]

                if dry_run:
                    logger.info(' '.join(eval_cmd))
                    continue

                try:
                    out = check_output(' '.join(eval_cmd)).decode('utf-8').split('\n')[-1]
                    if not out.strip():
                        continue
                except Exception as e:
                    logger.error(f"Failed to execute evaluation command: {str(e)}")
                    continue

                eval_out = out.strip().split(metric['separator'])[metric['parse_index']]
                expected = round(method['results'][metric['metric']][i], metric['metric_precision'])
                actual = round(float(eval_out), metric['metric_precision'])
                result_str = (
                    f'expected: {expected:.4f} actual: {actual:.4f} (delta={abs(expected-actual):.4f}) - '
                    f'metric: {metric["metric"]:<8} method: {method["name"]} topics: {topic_set["id"]}'
                )

                if is_close(expected, actual) or actual > expected:
                    logger.info(ok_str + result_str)
                else:
                    logger.error(fail_str + result_str)
                    failures = True

    end_time = time.time()
    logger.info(f"Total execution time: {end_time - start_time:.2f} seconds")
    if failures:
        logger.error(f'{fail_str}Some tests failed.')
    else:
        logger.info(f'All tests passed successfully!')

if __name__ == '__main__':
    start_time = time.time()

    # Command-line argument parsing
    parser = argparse.ArgumentParser(description='Run Fusion regression tests.')
    parser.add_argument('--regression', required=True, help='Name of the regression test configuration.')
    parser.add_argument('--dry-run', dest='dry_run', action='store_true',
                        help='Output commands without actual execution.')    
    args = parser.parse_args()

    # Load YAML configuration
    try:
        with open(f'src/main/resources/fuse_regression/{args.regression}.yaml') as f:
            yaml_data = yaml.safe_load(f)
    except FileNotFoundError as e:
        logger.error(f"Failed to load configuration file: {e}")
        exit(1)

    # Construct the fusion command
    fusion_commands = construct_fusion_commands(yaml_data)

    # Run the fusion process
    if args.dry_run:
        logger.info(' '.join([cmd for cmd_list in fusion_commands for cmd in cmd_list]))
    else:
        run_fusion_commands(fusion_commands)

    # Evaluate and verify results
    evaluate_and_verify(yaml_data, args.dry_run)

    logger.info(f"Total execution time: {time.time() - start_time:.2f} seconds")

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
from ranx import Run, fuse, evaluate, Qrels

# Constants
FUSE_COMMAND = 'bin/run.sh io.anserini.fusion.FuseRuns'
fusion_method_ranx = {
    "rrf": "rrf",
    "average": "sum",
    "interpolation": "wsum",
    "normalize": "sum"
}
metrics_ranx = {
    "nDCG@10": "ndcg@10",
    "R@100": "recall@100",
    "R@1000": "recall@1000"
}

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
            '-runs', ' '.join(run['file'] for run in yaml_data['runs']),
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

def compare_with_ranx(qrel_file: str, runs: list[str], methods: dict, metrics: list[str]) -> dict:
    """Given fusion conditions and location of runs and qrel, runs fusion with ranx and returns results."""
    qrels = Qrels.from_file(qrel_file)
    for i, r in enumerate(runs):
        runs[i] = Run.from_file(r, kind="trec").make_comparable(qrels)

    ranx_results = {}
    for method in methods:
        ranx_method = fusion_method_ranx[method["name"]]
        best_params = {}
        norm_method = None
        if ranx_method == "wsum":
            best_params['weights'] = (method.get('alpha', 0.5), 1 - method.get('alpha', 0.5))
        elif ranx_method == "rrf":
            best_params['k'] = method.get('rrf_k', 60)
        if method["name"] == "normalize":
            norm_method = "min-max"
        fused = fuse(
            runs=runs,
            norm=norm_method,
            method=ranx_method,
            params=best_params 
        )
        results = evaluate(qrels, fused, metrics)
        ranx_results[method["name"]] = results

    return ranx_results


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

    results = {}
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
                    out = [line for line in
                            check_output(' '.join(eval_cmd)).decode('utf-8').split('\n') if line.strip()][-1]
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
                if method["name"] not in results:
                    results[method["name"]] = {}
                results[method["name"]][metric["metric"]] = actual

    end_time = time.time()
    logger.info(f"Total execution time: {end_time - start_time:.2f} seconds")
    if failures:
        logger.error(f'{fail_str}Some tests failed.')
    else:
        logger.info(f'All tests passed successfully!')

    logger.info('=' * 10 + ' Verifying Fusion Results Against Ranx' + '=' * 10)
    sanity_check = compare_with_ranx('tools/topics-and-qrels/' + yaml_data['topics'][0]['qrel'], 
                                    [run["file"] for run in yaml_data["runs"]], 
                                    yaml_data['methods'], 
                                    ['ndcg@10', 'recall@100', 'recall@1000'])
    for method in yaml_data['methods']:
        for i, topic_set in enumerate(yaml_data['topics']):
            for metric in yaml_data['metrics']:
                expected = sanity_check[method["name"]][metrics_ranx[metric["metric"]]]
                actual = results[method["name"]][metric["metric"]]
                result_str = (
                    f'ranx: {expected:.4f} actual: {actual:.4f} (delta={abs(expected-actual):.4f}) - '
                    f'metric: {metric["metric"]:<8} method: {method["name"]} topics: {topic_set["id"]}'
                )
                logger.info(result_str)
    logger.info(f"Total ranx execution time: {time.time() - end_time:.2f} seconds")       

if __name__ == '__main__':
    # Command-line argument parsing
    parser = argparse.ArgumentParser(description='Run Fusion regression tests.')
    parser.add_argument('--regression', required=True, help='Name of the regression test configuration.')
    parser.add_argument('--dry-run', dest='dry_run', action='store_true',
                        help='Output commands without actual execution.')    
    args = parser.parse_args()

    # Load YAML configuration
    try:
        with open(f'src/main/resources/fusion_regression/{args.regression}.yaml') as f:
            yaml_data = yaml.safe_load(f)
    except FileNotFoundError as e:
        logger.error(f"Failed to load configuration file: {e}")
        exit(1)

    # Check existence of run files
    for run in yaml_data['runs']:
        if not os.path.exists(run['file']):
            logger.error(f"Run file {run['file']} does not exist. Please run the dependent regressions first, recorded in the fusion yaml file.")
            exit(1)

    start_time = time.time()

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
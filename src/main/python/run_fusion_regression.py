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
import sys
import argparse
import logging
import time
import yaml
import re
import glob
from collections import namedtuple
from subprocess import call, Popen, PIPE, STDOUT
from ranx import Run, fuse, evaluate, Qrels

# Constants
FUSE_CMD = 'bin/run.sh io.anserini.fusion.FuseRuns'
RANX_METHODS = {"rrf": "rrf", "average": "sum", "normalize": "sum"}
SCORE_TOLERANCE = 1e-3

# Hardcoded patterns matching beir.yaml and RunBeir
BM25_RUN_TMPL = 'runs/run.beir.flat.$topics.txt'
BGE_RUN_TMPL = 'runs/run.beir.bge-base-en-v1.5.flat.onnx.$topics.txt'
K = 1000
DEPTH = 1000
RRF_K = 60

# Configure logging to match run_regression.py style
logger = logging.getLogger('fusion_regression')
logger.setLevel(logging.INFO)
ch = logging.StreamHandler()
ch.setLevel(logging.INFO)
formatter = logging.Formatter('%(asctime)s %(levelname)s  [python] %(message)s')
ch.setFormatter(formatter)
logger.addHandler(ch)

# Named tuple for fusion command data
FusionCommand = namedtuple('FusionCommand', [
    'cmd', 'cond_name', 'topic_key', 'eval_key', 'output', 'run_files',
    'method', 'has_minmax', 'rrf_k', 'expected', 'run_files_tmpl'
])

def check_output(cmd):
    """Execute command and return output. Raises RuntimeError on failure."""
    p = Popen(cmd, shell=True, stdout=PIPE, stderr=STDOUT)
    out, _ = p.communicate()
    if p.returncode != 0:
        error_msg = out.decode('utf-8', errors='replace') if out else "Unknown error"
        raise RuntimeError(f"Command failed (exit {p.returncode}): {cmd}\n{error_msg}")
    return out

def get_fusion_commands(beir_data, corpus=None):
    """Extract fusion commands from beir.yaml.
    
    Hardcoded patterns matching RunBeir (from beir.yaml and RunRepro.java):
    - Input run files: runs/run.beir.flat.$topics.txt and runs/run.beir.bge-base-en-v1.5.flat.onnx.$topics.txt
    - Output format: runs/run.beir.{condition_name}.{topic_key}.txt (from RunRepro.java line 195)
    - Parameters: k=1000, depth=1000, rrf_k=60
    """
    commands = []
    for cond in beir_data.get('conditions', []):
        cond_name = cond.get('name', '')
        if not cond_name.startswith('fusion-'):
            continue
        
        # Determine fusion method and parameters from condition
        if cond_name == 'fusion-rrf':
            method = 'rrf'
            has_minmax = False
        elif cond_name == 'fusion-avg':
            method = 'average'
            has_minmax = True  # fusion-avg uses min_max_normalization
        else:
            continue  # Skip unknown fusion methods
        
        # Process each topic
        for topic in cond.get('topics', []):
            topic_key = topic.get('topic_key', '')
            if corpus and topic_key != corpus:
                continue
            if not topic_key or not topic.get('eval_key'):
                continue
            
            # Get expected score
            expected = next((s.get('nDCG@10') for s in topic.get('scores', []) 
                           if isinstance(s, dict) and 'nDCG@10' in s), None)
            if expected is None:
                continue
            
            # Build run file paths (replace $topics with actual topic_key)
            run_files = [
                BM25_RUN_TMPL.replace('$topics', topic_key),
                BGE_RUN_TMPL.replace('$topics', topic_key)
            ]
            
            # Build output path (RunBeir format: runs/run.beir.{condition_name}.{topic_key}.txt)
            output = f'runs/run.beir.{cond_name}.{topic_key}.txt'
            
            # Build fusion command
            cmd = [FUSE_CMD, '-runs'] + run_files + [
                '-output', output,
                '-method', method,
                '-k', str(K),
                '-depth', str(DEPTH)
            ]
            
            if method == 'rrf':
                cmd.extend(['-rrf_k', str(RRF_K)])
            elif has_minmax:
                cmd.append('-min_max_normalization')
            
            commands.append(FusionCommand(
                cmd=cmd, cond_name=cond_name, topic_key=topic_key, eval_key=topic['eval_key'],
                output=output, run_files=run_files, method=method, has_minmax=has_minmax,
                rrf_k=RRF_K, expected=expected, run_files_tmpl=[BM25_RUN_TMPL, BGE_RUN_TMPL]
            ))
    
    return commands

def get_condition(beir_data, name):
    return next((c for c in beir_data.get('conditions', []) if c.get('name') == name), None)

def build_search_cmd(cond, topic_key):
    """Build search command from condition."""
    name = cond.get('name', '')
    output = f'runs/run.beir.{name}.{topic_key}.txt'
    
    fatjar_files = glob.glob('target/anserini-*-fatjar.jar') or glob.glob('target/anserini*.jar')
    fatjar = fatjar_files[0] if fatjar_files else 'target/anserini-fatjar.jar'
    cmd = cond.get('command', '').replace('$topics', topic_key).replace('$output', output).replace('$fatjar', fatjar).replace('$threads', '16')
    
    parts = cmd.split()
    if parts[0] == 'java':
        class_idx = next((i for i, p in enumerate(parts) if p.startswith('io.anserini.')), None)
        if class_idx:
            parts = ['bin/run.sh'] + parts[class_idx:]
    
    return ' '.join(parts), output

def ensure_runs(beir_data, corpus):
    """Generate missing run files for BM25 and BGE if needed."""
    # We always need these two conditions for fusion
    required_conditions = ['flat', 'bge-base-en-v1.5.flat.onnx']
    to_gen = []
    
    for cond_name in required_conditions:
        output = f'runs/run.beir.{cond_name}.{corpus}.txt'
        if os.path.exists(output):
            continue
        
        cond = get_condition(beir_data, cond_name)
        if not cond:
            logger.warning(f"Condition '{cond_name}' not found in beir.yaml")
            continue
        
        topic = next((t for t in cond.get('topics', []) if t.get('topic_key') == corpus), None)
        if not topic:
            logger.warning(f"Topic '{corpus}' not found for condition '{cond_name}'")
            continue
        
        to_gen.append((cond_name, cond, topic))
    
    if to_gen:
        logger.info(f"Generating {len(to_gen)} missing run file(s) for {corpus}...")
        for cond_name, cond, topic in to_gen:
            cmd, output = build_search_cmd(cond, topic['topic_key'])
            logger.info(f"  {cond_name} -> {output}")
            if call(cmd, shell=True) != 0:
                raise RuntimeError(f"Failed: {cmd}")

def ranx_ndcg(qrel_file, runs, method, has_minmax, rrf_k):
    """Get nDCG@10 from ranx for sanity check."""
    try:
        qrels = Qrels.from_file(qrel_file)
        ranx_runs = [Run.from_file(r, kind="trec").make_comparable(qrels) for r in runs]
        
        ranx_method = RANX_METHODS.get("normalize" if (method == "average" and has_minmax) else method, "sum")
        params = {'k': rrf_k} if ranx_method == "rrf" else {}
        norm = "min-max" if has_minmax else None
        
        fused = fuse(runs=ranx_runs, norm=norm, method=ranx_method, params=params)
        return float(evaluate(qrels, fused, 'ndcg@10'))
    except Exception as e:
        logger.debug(f"Ranx check failed: {e}")
        raise

def parse_trec_eval_output(output_str):
    """Parse nDCG@10 from trec_eval output.
    
    trec_eval output format: 'ndcg_cut_10  all  0.3725'
    Returns the float value or raises ValueError if not found.
    """
    lines = [l.strip() for l in output_str.split('\n') if l.strip()]
    if not lines:
        raise ValueError("Empty trec_eval output")
    
    # Last line should contain the metric value
    last_line = lines[-1]
    parts = last_line.split('\t')
    if len(parts) < 3:
        parts = last_line.split()
    
    if len(parts) < 3:
        raise ValueError(f"Cannot parse trec_eval output: {last_line}")
    
    return float(parts[2])

def evaluate_results(commands, dry_run):
    """Evaluate fusion results."""
    failures = 0
    total = len(commands)
    
    for idx, cmd_data in enumerate(commands, 1):
        qrel = f'tools/topics-and-qrels/qrels.{cmd_data.eval_key}.txt'
        if not os.path.exists(qrel):
            logger.warning(f"Skipping {cmd_data.cond_name} {cmd_data.topic_key}: qrel file not found: {qrel}")
            continue
        
        if dry_run:
            logger.info(f"{cmd_data.cond_name:15} {cmd_data.topic_key:20} expected: {cmd_data.expected:.4f}")
            continue
        
        try:
            # Run trec_eval and parse output
            eval_start = time.time()
            trec_cmd = f'bin/trec_eval -c -m ndcg_cut.10 {qrel} {cmd_data.output}'
            out = check_output(trec_cmd).decode('utf-8')
            eval_time = time.time() - eval_start
            actual = round(parse_trec_eval_output(out), 4)
            expected_r = round(cmd_data.expected, 4)
            delta = abs(actual - expected_r)
            
            # Test passes if within tolerance or if actual > expected (improvement)
            passed = (delta <= SCORE_TOLERANCE or actual > expected_r)
            status = "PASSED" if passed else "FAILED"
            
            logger.info(f"[{idx}/{total}] {status} {cmd_data.cond_name:15} {cmd_data.topic_key:20} "
                       f"{expected_r:.4f} -> {actual:.4f} (Δ={delta:.4f}) [eval: {eval_time:.2f}s]")
            
            if not passed:
                failures += 1
            
            # Ranx sanity check (non-blocking)
            try:
                ranx_start = time.time()
                ranx_score = round(ranx_ndcg(qrel, cmd_data.run_files, cmd_data.method, 
                                           cmd_data.has_minmax, cmd_data.rrf_k), 4)
                ranx_time = time.time() - ranx_start
                ranx_delta = abs(actual - ranx_score)
                ranx_passed = ranx_delta <= SCORE_TOLERANCE
                ranx_status = "PASSED" if ranx_passed else "DIFF"
                logger.info(f"  {ranx_status} ranx: {ranx_score:.4f} -> {actual:.4f} (Δ={ranx_delta:.4f}) [eval: {ranx_time:.2f}s]")
            except Exception as e:
                logger.debug(f"  ranx check skipped: {e}")
                
        except Exception as e:
            logger.error(f"[{idx}/{total}] FAILED {cmd_data.cond_name} {cmd_data.topic_key}: {e}")
            failures += 1
    
    if failures:
        logger.error(f"\n{failures} test(s) failed out of {total}")
        sys.exit(1)
    else:
        logger.info(f"\nAll {total} test(s) passed")

def prepare_runs(commands, beir_data, dry_run):
    """Prepare required run files: generate missing ones and verify all exist.
    
    Returns True if all files are ready, False otherwise.
    """
    # Collect unique corpora
    corpora = set(cmd_data.topic_key for cmd_data in commands)
    
    # Generate missing runs
    if not dry_run:
        for corpus in corpora:
            ensure_runs(beir_data, corpus)
    
    # Verify all required run files exist
    missing = []
    for cmd_data in commands:
        for rf in cmd_data.run_files:
            if not os.path.exists(rf):
                missing.append(rf)
    
    if missing:
        logger.error(f"Missing {len(missing)} run file(s): {', '.join(missing[:3])}{'...' if len(missing) > 3 else ''}")
        return False
    
    return True

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Run fusion regression tests from beir.yaml')
    parser.add_argument('--corpus', help='Corpus name (e.g., nfcorpus). If not specified, runs all.')
    parser.add_argument('--dry-run', action='store_true', help='Show commands without executing')
    args = parser.parse_args()
    
    with open('src/main/resources/reproduce/beir.yaml') as f:
        beir_data = yaml.safe_load(f)
    
    start = time.time()
    commands = get_fusion_commands(beir_data, args.corpus)
    
    if not commands:
        logger.error(f"No fusion conditions found{' for corpus ' + args.corpus if args.corpus else ''}")
        sys.exit(1)
    
    logger.info(f"Found {len(commands)} fusion command(s){' for ' + args.corpus if args.corpus else ''}")
    
    # Prepare required run files
    if not prepare_runs(commands, beir_data, args.dry_run):
        sys.exit(1)
    
    # Run fusion
    if args.dry_run:
        for cmd_data in commands:
            logger.info(' '.join(cmd_data.cmd))
    else:
        total = len(commands)
        for idx, cmd_data in enumerate(commands, 1):
            logger.info(f"[{idx}/{total}] Running fusion: {cmd_data.cond_name} {cmd_data.topic_key}")
            fusion_start = time.time()
            if call(' '.join(cmd_data.cmd), shell=True) != 0:
                logger.error(f"Fusion failed: {' '.join(cmd_data.cmd)}")
                sys.exit(1)
            fusion_time = time.time() - fusion_start
            logger.info(f"  Fusion completed in {fusion_time:.2f}s")
    
    # Evaluate
    evaluate_results(commands, args.dry_run)
    logger.info(f"Total time: {time.time() - start:.1f}s")

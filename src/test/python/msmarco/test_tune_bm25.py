"""Regression tests for the MS MARCO tuning workflow (standard library only)."""
import argparse
import importlib.util
from pathlib import Path
import subprocess
import tempfile
import sys
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[4]
spec = importlib.util.spec_from_file_location('tune_bm25', ROOT / 'src/main/python/msmarco/tune_bm25.py')
tune = importlib.util.module_from_spec(spec)
spec.loader.exec_module(tune)


class TuneBm25Test(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        (ROOT / 'tmp').mkdir(exist_ok=True)

    def test_conversion_preserves_document_ids_and_rank_order(self):
        with tempfile.TemporaryDirectory(dir=ROOT / 'tmp') as directory:
            source, output = Path(directory) / 'run.txt', Path(directory) / 'run.trec'
            source.write_text('1\tD1\t1\n1\tD2\t2\n')
            subprocess.run([sys.executable, str(ROOT / 'src/main/python/msmarco/convert_msmarco_to_trec_run.py'),
                            '--input', str(source), '--output', str(output)], check=True, capture_output=True)
            rows = [line.split() for line in output.read_text().splitlines()]
            self.assertEqual([row[2] for row in rows], ['D1', 'D2'])
            self.assertEqual([float(row[4]) for row in rows], [1.0, 0.5])

    def test_inclusive_decimal_grid(self):
        self.assertEqual(tune.grid('0.6:1.2:0.1'), [0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2])
        self.assertEqual(tune.grid('0.9,0.5,0.9'), [0.5, 0.9])
        with self.assertRaises(argparse.ArgumentTypeError):
            tune.grid('0.1:1.0:0')

    def test_sample_denominator_excludes_other_training_queries(self):
        with tempfile.TemporaryDirectory(dir=ROOT / 'tmp') as directory:
            directory = Path(directory)
            queries, output = directory / 'queries.tsv', directory / 'qrels.txt'
            queries.write_text('1\tfirst\n2\tunjudged\n')
            count = tune.sample_qrels(queries, {'1': {'D1': 1}, '3': {'D3': 1}}, output)
            self.assertEqual(count, {'queries': 2, 'judged': 1})
            self.assertEqual(output.read_text(), '1 0 D1 1\n')

    def test_sample_without_judgments_fails(self):
        with tempfile.TemporaryDirectory(dir=ROOT / 'tmp') as directory:
            queries = Path(directory) / 'queries.tsv'
            queries.write_text('1\tquery\n')
            with self.assertRaisesRegex(ValueError, 'no queries have judgments'):
                tune.sample_qrels(queries, {'2': {'D2': 1}}, Path(directory) / 'qrels')

    def test_zero_scores_and_ties_have_deterministic_winner(self):
        scores = {'recall_1000': 0.0, 'recip_rank': 0.0, 'map': 0.0}
        rows = [dict(k1=1.0, b=0.7, scores=scores), dict(k1=0.6, b=0.5, scores=scores)]
        self.assertTrue(all(row['k1'] == 0.6 for row in tune.winners(rows).values()))

    def test_mrr_cutoff_does_not_truncate_recall_or_map(self):
        outputs = [subprocess.CompletedProcess([], 0, 'map all 0.25\nrecall_1000 all 0.8\n'),
                   subprocess.CompletedProcess([], 0, 'recip_rank all 0.2\n')]
        with patch.object(tune.subprocess, 'run', side_effect=outputs) as run:
            self.assertEqual(tune.evaluate('qrels with spaces', 'run with spaces', 100)['recip_rank'], 0.2)
            first, second = [call.args[0] for call in run.call_args_list]
            self.assertNotIn('-M', first)
            self.assertEqual(second[second.index('-M') + 1], '100')
            self.assertEqual(first[-2:], ['qrels with spaces', 'run with spaces'])

    def test_silent_native_evaluation_failure_is_rejected(self):
        with patch.object(tune.subprocess, 'run', return_value=subprocess.CompletedProcess([], 0, '')):
            with self.assertRaisesRegex(RuntimeError, 'required metrics'):
                tune.evaluate('qrels', 'run', 10)


if __name__ == '__main__':
    unittest.main()

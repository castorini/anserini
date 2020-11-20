#
# Pyserini: Python interface to the Anserini IR toolkit built on Lucene
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

"""Download, index, and verify a particular CORD-19 release from AI2."""

import argparse
import os
import shutil
import sys
import subprocess
import tarfile
from urllib.request import urlretrieve

from tqdm import tqdm

sys.path.insert(0, './')


# https://gist.github.com/leimao/37ff6e990b3226c2c9670a2cd1e4a6f5
class TqdmUpTo(tqdm):
    def update_to(self, b=1, bsize=1, tsize=None):
        """
        b  : int, optional
            Number of blocks transferred so far [default: 1].
        bsize  : int, optional
            Size of each block (in tqdm units) [default: 1].
        tsize  : int, optional
            Total size (in tqdm units). If [default: None] remains unchanged.
        """
        if tsize is not None:
            self.total = tsize
        self.update(b * bsize - self.n)  # will also set self.n = b * bsize


def download_url(url, save_dir):
    filename = url.split('/')[-1]
    with TqdmUpTo(unit='B', unit_scale=True, unit_divisor=1024, miniters=1, desc=filename) as t:
        urlretrieve(url, filename=os.path.join(save_dir, filename), reporthook=t.update_to)


def download_collection(date):
    print(f'Downloading CORD-19 release of {date}...')
    collection_dir = f'collections/'
    tarball_url = f'https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/historical_releases/cord-19_{date}.tar.gz'
    tarball_local = os.path.join(collection_dir, f'cord-19_{date}.tar.gz')

    if not os.path.exists(tarball_local):
        print(f'Fetching {tarball_url}...')
        download_url(tarball_url, collection_dir)
    else:
        print(f'{tarball_local} already exists, skipping download.')

    print(f'Extracting {tarball_local} into {collection_dir}')
    tarball = tarfile.open(tarball_local)
    tarball.extractall(collection_dir)
    tarball.close()

    docparses = os.path.join(collection_dir, date, 'document_parses.tar.gz')
    collection_base = os.path.join(collection_dir, date)

    print(f'Extracting {docparses} into {collection_base}...')
    tarball = tarfile.open(docparses)
    tarball.extractall(collection_base)
    tarball.close()

    print(f'Renaming {collection_base}')
    os.rename(collection_base, os.path.join(collection_dir, f'cord19-{date}'))


def build_indexes(date):
    if not os.path.isdir(f'indexes/lucene-index-cord19-abstract-{date}'):
        print(f'Building abstract index...')
        os.system(f'sh target/appassembler/bin/IndexCollection -collection Cord19AbstractCollection ' +
                  f'-generator Cord19Generator -threads 8 -input collections/cord19-{date} ' +
                  f'-index indexes/lucene-index-cord19-abstract-{date} ' +
                  f'-storePositions -storeDocvectors -storeContents -storeRaw -optimize ' +
                  f' | tee logs/log.cord19-abstract.{date}.txt')
    else:
        print('Abstract index appears to have been built, skipping.')

    if not os.path.isdir(f'indexes/lucene-index-cord19-full-text-{date}'):
        print(f'Building full-text index...')
        os.system(f'sh target/appassembler/bin/IndexCollection -collection Cord19FullTextCollection ' +
                  f'-generator Cord19Generator -threads 8 -input collections/cord19-{date} ' +
                  f'-index indexes/lucene-index-cord19-full-text-{date} ' +
                  f'-storePositions -storeDocvectors -storeContents -storeRaw -optimize ' +
                  f' | tee logs/log.cord19-full-text.{date}.txt')
    else:
        print('Full-text index appears to have been built, skipping.')

    if not os.path.isdir(f'indexes/lucene-index-cord19-paragraph-{date}'):
        print(f'Building paragraph index...')
        os.system(f'sh target/appassembler/bin/IndexCollection -collection Cord19ParagraphCollection ' +
                  f'-generator Cord19Generator -threads 8 -input collections/cord19-{date} ' +
                  f'-index indexes/lucene-index-cord19-paragraph-{date} ' +
                  f'-storePositions -storeDocvectors -storeContents -storeRaw -optimize ' +
                  f' | tee logs/log.cord19-paragraph.{date}.txt')
    else:
        print('Paragraph index appears to have been built, skipping.')


def evaluate_run(run):
    qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt'
    metrics = {}
    output = subprocess.check_output(
        f'tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 {qrels} runs/{run}', shell=True)

    arr = output.split()
    metrics[arr[0].decode('utf-8')] = float(arr[2])

    output = subprocess.check_output(f'python tools/eval/measure_judged.py --qrels {qrels} ' +
                                     f'--cutoffs 10 100 1000 --run runs/{run}', shell=True)

    arr = output.split()
    metrics[arr[0].decode('utf-8')] = float(arr[2])

    return metrics


def verify_indexes(date):
    topics = 'src/main/resources/topics-and-qrels/topics.covid-round3.xml'
    whitelist = 'src/main/resources/topics-and-qrels/docids.covid.round3.txt'

    print('Verifying abstract index...')
    abstract_index = f'indexes/lucene-index-cord19-abstract-{date} '
    os.system(f'sh target/appassembler/bin/SearchCollection -index {abstract_index} -topicreader Covid ' +
              f'-topics {topics} -topicfield query+question ' +
              f'-removedups -bm25 -hits 1000 -output runs/verify.{date}.abstract.txt')
    os.system(f'python tools/scripts/filter_run.py --whitelist {whitelist} --k 1000 ' +
              f'--input runs/verify.{date}.abstract.txt --output runs/verify.{date}.abstract.filtered.txt')
    abstract_metrics = evaluate_run(f'verify.{date}.abstract.filtered.txt')

    print('Verifying full-text index...')
    full_index = f'indexes/lucene-index-cord19-full-text-{date} '
    os.system(f'sh target/appassembler/bin/SearchCollection -index {full_index} -topicreader Covid ' +
              f'-topics {topics} -topicfield query+question ' +
              f'-removedups -bm25 -hits 1000 -output runs/verify.{date}.full-text.txt')
    os.system(f'python tools/scripts/filter_run.py --whitelist {whitelist} --k 1000 ' +
              f'--input runs/verify.{date}.full-text.txt --output runs/verify.{date}.full-text.filtered.txt')
    full_metrics = evaluate_run(f'verify.{date}.full-text.filtered.txt')

    print('Verifying paragraph index...')
    paragraph_index = f'indexes/lucene-index-cord19-paragraph-{date} '
    os.system(f'sh target/appassembler/bin/SearchCollection -index {paragraph_index} -topicreader Covid ' +
              f'-topics {topics} -topicfield query+question ' +
              f'-selectMaxPassage -bm25 -hits 1000 -output runs/verify.{date}.paragraph.txt')
    os.system(f'python tools/scripts/filter_run.py --whitelist {whitelist} --k 1000 ' +
              f'--input runs/verify.{date}.paragraph.txt --output runs/verify.{date}.paragraph.filtered.txt')
    paragraph_metrics = evaluate_run(f'verify.{date}.paragraph.filtered.txt')

    print()
    print('## Effectiveness Summary')
    print()
    print(f'CORD-19 release: {date}')
    print(f'Topics/Qrels: TREC-COVID Round 3')
    print(f'Whitelist: TREC-COVID Round 3 valid docids')
    print()
    print('                    NDCG@10  Judged@10')
    print(f'Abstract index       {abstract_metrics["ndcg_cut_10"]:.4f}    {abstract_metrics["judged_cut_10"]:.4f}')
    print(f'Full-text index      {full_metrics["ndcg_cut_10"]:.4f}    {full_metrics["judged_cut_10"]:.4f}')
    print(f'Paragraph index      {paragraph_metrics["ndcg_cut_10"]:.4f}    {paragraph_metrics["judged_cut_10"]:.4f}')


def main(args):
    if not args.all and not (args.download or args.index or args.verify):
        print('Must specify --all or one of {--download, --index, --verify}.')
    else:
        if args.all or args.download:
            collection_dir = f'collections/cord19-{args.date}'
            if not args.force and os.path.exists(collection_dir):
                print('Collection exists; not redownloading collection. ' +
                      'Use --force to remove existing collection and redownload.')
            else:
                if os.path.exists(collection_dir):
                    print('Removing existing collection...')
                    shutil.rmtree(collection_dir)
                download_collection(args.date)
        if args.all or args.index:
            abstract_index = f'indexes/lucene-index-cord19-abstract-{args.date}'
            full_index = f'indexes/lucene-index-cord19-full-text-{args.date}'
            paragraph_index = f'indexes/lucene-index-cord19-paragraph-{args.date}'
            if not args.force and (os.path.isdir(abstract_index) or
                                   os.path.isdir(full_index) or
                                   os.path.isdir(paragraph_index)):
                print('Indexes exist; not reindexing. ' +
                      'Use --force to index and overwrite existing indexes.')
            else:
                if os.path.exists(abstract_index):
                    print(f'Removing index at {abstract_index}...')
                    shutil.rmtree(abstract_index)
                if os.path.exists(full_index):
                    print(f'Removing index at {full_index}...')
                    shutil.rmtree(full_index)
                if os.path.exists(paragraph_index):
                    print(f'Removing index at {paragraph_index}...')
                    shutil.rmtree(paragraph_index)
                build_indexes(args.date)
        if args.all or args.verify:
            if not args.force and (os.path.exists(f'runs/verify.{args.date}.abstract.filtered.txt') or
                                   os.path.exists(f'runs/verify.{args.date}.full-text.filtered.txt') or
                                   os.path.exists(f'runs/verify.{args.date}.paragraph.filtered.txt')):
                print('Runs exist; not rerunning retrieval. ' +
                      'Use --force to run retrieval and overwrite existing run files.')
            else:
                verify_indexes(args.date)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--date', type=str, metavar='YYYY-MM-DD', required=True, help='Date of the CORD-19 release.')
    parser.add_argument('--all',  action='store_true', help='Download, index, and verify a CORD-19 release.')
    parser.add_argument('--download',  action='store_true', help='Download a CORD-19 release.')
    parser.add_argument('--index',  action='store_true', help='Build abstract, full-text, and paragraph indexes.')
    parser.add_argument('--verify',  action='store_true', help='Verify indexes with TREC-COVID data.')
    parser.add_argument('--force',  action='store_true', help='Overwrite existing data.')

    main(parser.parse_args())

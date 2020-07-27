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

"""Download pre-built docTTTTTquery expanded CORD-19 indexes."""

import argparse
import sys

sys.path.insert(0, '../pyserini/')

import pyserini.util


all_indexes = {
    '2020-06-19': ['https://www.dropbox.com/s/jqdcub1newrb5pa/lucene-index-cord19-abstract-docT5query-2020-06-19.tar.gz?dl=1',
                   'https://www.dropbox.com/s/bmdbg103zmjufnj/lucene-index-cord19-full-text-docT5query-2020-06-19.tar.gz?dl=1',
                   'https://www.dropbox.com/s/7dajfdff192dy9k/lucene-index-cord19-paragraph-docT5query-2020-06-19.tar.gz?dl=1'],
    '2020-07-16': ['https://www.dropbox.com/s/gzq1d305oe465t1/lucene-index-cord19-abstract-docT5query-2020-07-16.tar.gz?dl=1',
                   'https://www.dropbox.com/s/63gbbzqossemkzk/lucene-index-cord19-full-text-docT5query-2020-07-16.tar.gz?dl=1',
                   'https://www.dropbox.com/s/9fml7m2si7qbm17/lucene-index-cord19-paragraph-docT5query-2020-07-16.tar.gz?dl=1']
}


def main(args):
    if args.date not in all_indexes:
        print(f'Unknown index {args.date}')
    else:
        for index in all_indexes[args.date]:
            pyserini.util.download_and_unpack_index(index, force=args.force)
        print('Done!')


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--date', type=str, metavar='YYYY-MM-DD', required=True, help='Date of the CORD-19 release.')
    parser.add_argument('--force',  action='store_true', help='Overwrite existing data.')

    main(parser.parse_args())

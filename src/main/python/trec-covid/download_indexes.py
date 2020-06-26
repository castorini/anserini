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

"""Download pre-built CORD-19 indexes."""

import argparse
import os
import shutil
import tarfile


def download_indexes(indexes, force: bool):
    for url in indexes:
        file = url.split('/')[-1]
        index_name = file.split('.')[0]
        index_dir = f'collections/{index_name}'
        local_tarball = f'indexes/{file}'

        print(f'Index: {index_name}')

        if force:
            if os.path.isdir(index_dir):
                print(f'Removing {index_dir}')
                shutil.rmtree(index_dir)
            if os.path.exists(local_tarball):
                print(f'Removing {local_tarball}')
                os.remove(local_tarball)
        elif os.path.isdir(index_dir) or os.path.exists(local_tarball):
            print(f'{index_dir} or {local_tarball} already exists, skipping!')
            continue

        print(f'Downloading index at {url}...')
        os.system(f'wget {url} -P indexes/')

        print(f'Extracting index...')
        tarball = tarfile.open(f'indexes/{file}')
        tarball.extractall('indexes/')
        tarball.close()


all_indexes = {
    '2020-05-19': ['https://www.dropbox.com/s/3ld34ms35zfb4m9/lucene-index-cord19-abstract-2020-05-19.tar.gz',
                   'https://www.dropbox.com/s/qih3tjsir3xulrn/lucene-index-cord19-full-text-2020-05-19.tar.gz',
                   'https://www.dropbox.com/s/7z8szogu5neuhqe/lucene-index-cord19-paragraph-2020-05-19.tar.gz'],
    '2020-06-19': ['https://www.dropbox.com/s/bj6lx80wwiy5hxf/lucene-index-cord19-abstract-2020-06-19.tar.gz',
                   'https://www.dropbox.com/s/vkhhxj8u36rgdu9/lucene-index-cord19-full-text-2020-06-19.tar.gz',
                   'https://www.dropbox.com/s/yk6egw6op4jccpi/lucene-index-cord19-paragraph-2020-06-19.tar.gz']
}


def main(args):
    if args.date not in all_indexes:
        print(f'Unknown index {args.date}')
    else:
        download_indexes(all_indexes[args.date], args.force)
        print('Done!')


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--date', type=str, metavar='YYYY-MM-DD', required=True, help='Date of the CORD-19 release.')
    parser.add_argument('--force',  action='store_true', help='Overwrite existing data.')

    main(parser.parse_args())

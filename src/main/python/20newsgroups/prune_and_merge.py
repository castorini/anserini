# -*- coding: utf-8 -*-
"""
Anserini: A Lucene toolkit for replicable information retrieval research

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

import argparse
import os
from typing import List
import shutil


def mkdir_if_not_exist(directory: str) -> None:
    if not os.path.exists(directory):
        os.makedirs(directory)


def remove_folder(directory: str) -> None:
    if os.path.exists(directory):
        shutil.rmtree(directory)


def prune_and_merge(paths: List[str], out: str) -> None:
    remove_folder(out)
    mkdir_if_not_exist(out)

    # Get all categories
    num_docs = 0
    categories = set()
    for path in paths:
        for _, dirs, files in os.walk(path, topdown=False):
            for category in dirs:
                categories.add(category)
            num_docs += len(files)

    print(f'Detected {num_docs} docs in {len(categories)} categories')

    # mkdir for each category
    for category in categories:
        mkdir_if_not_exist(os.path.join(out, category))

    new_doc_id = 0
    num_docs_copied = 0
    for path in paths:
        for root, _, files in os.walk(path, topdown=False):
            for doc_id in files:
                if num_docs_copied % 1000 == 0:
                    print(f'Copy in progress: {num_docs_copied}/{num_docs}')
                num_docs_copied += 1
                category = root.split('/')[-1]

                from_path = os.path.join(root, doc_id)
                # assign new unique doc id
                to_path = os.path.join(root, f'{new_doc_id}')
                # copy to merged folder
                new_path = os.path.join(out, category, f'{new_doc_id}')
                shutil.copyfile(from_path, new_path)
                shutil.move(from_path, to_path)
                new_doc_id += 1

    print(to_path)
    print(f"Processed {num_docs_copied} docs")


if __name__ == "__main__":
    # Prune: re-name docs starting from 0 to avoid two docs with the same id
    parser = argparse.ArgumentParser(
        description='prune and merge the 20 newsgroups train & test set')

    parser.add_argument('--paths', type=str, nargs='+',
                        default=[], required=True, help='paths to train/test folders')
    parser.add_argument('--out', type=str,
                        default="./20news", required=False, help='the output path of the merged folder')

    args = parser.parse_args()
    prune_and_merge(args.paths, args.out)

    print(f'Prune & Merge done -> {args.out}')

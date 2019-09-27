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

"""
This script is used for converting the cross-lingual IR corpus
into json format, which can be easily indexed by Anserini.

The jsonline format of Anserini is as follows:

{"id": "doc1", "contents": "string1"}

Currently the data we have:
  - ZH: gigaword-xin.2002-06.zh-cleaned.xml
"""

import argparse
import json
import os

ZH_CORPUS_NAME = "gigaword-xin.2002-06.zh-cleaned.xml"


def zh2json(file_path, output_path):
    """
    Processing rules:
        1. If two lines are successive, then concatenate them without space
        2. If two lines are separated with two lines, then separate them with period 。.
    This rules do not matter for passage level indexing, but if when we do the
    sentence level indexing, it will affect the performance.

    :param file_path:
    :return:
    """
    fout = open(output_path, 'w')
    counter = 0
    with open(os.path.join(file_path, ZH_CORPUS_NAME)) as fin:
        while True:
            line = fin.readline()
            if line.startswith("<DOC>"):
                # We assume the nextline of "<DOC>" label line is
                # "<DOCNO>" line.
                example = {}
                line = fin.readline()
                if line.startswith("<DOCNO>"):
                    line = line.replace("<DOCNO>", "").replace("</DOCNO>", "").strip()
                    example["id"] = line
                else:
                    print("The line is {}, but we assume it is <DOCNO> line".format(line))
                    exit()
                # Read contents
                example["contents"] = []
                line = fin.readline()
                while (not line.startswith("</DOC>")):
                    line = line.strip()
                    if len(line) == 0:
                        example["contents"].append("。")
                    else:
                        example["contents"].append(line)
                    line = fin.readline()
                example["contents"] = "".join(example["contents"])
                fout.write(json.dumps(example) + "\n")
                counter += 1
                if counter % 10000 == 0:
                    print("Dump {} examples".format(counter))
            elif not line:
                break
        print("Done")

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--language", type=str, choices=["zh"])
    parser.add_argument("--corpus_directory", type=str)
    parser.add_argument("--output_path", type=str)
    args = parser.parse_args()

    dir = os.path.dirname(args.output_path)
    if not os.path.exists(dir):
        os.makedirs(dir)

    if args.language == "zh":
        zh2json(args.corpus_directory, args.output_path)

"""
This script is used for converting the cross-lingual IR corpus
into json format, which can be easily indexed by Anserini.

The jsonline format of Anserini is as follows:

{"id": "doc1", "contents": "string1"}

Currently the data we have:
  - ZH: gigaword-xin.2002-06.zh-cleaned.xml
  - AR: ldc2001t55.ar-cleaned.xml
  - FR: lemonde94-95+sda94-95.fr-cleaned.xml
"""

import argparse
import json
import os

CORPUS_NAME = {"zh": "gigaword-xin.2002-06.zh-cleaned.xml",
               "ar": "ldc2001t55.ar-cleaned.xml",
               "fr": "lemonde94-95+sda94-95.fr-cleaned.xml"}


def corpus2json(language, file_path, output_path):
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
    with open(os.path.join(file_path, CORPUS_NAME[language])) as fin:
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
                        if language == "zh":
                            example["contents"].append("。")
                        else:
                            example["contents"].append(". ")
                    else:
                        example["contents"].append(line)
                    line = fin.readline()
                if language == "zh":
                    example["contents"] = "".join(example["contents"])
                else:
                    example["contents"] = " ".join(example["contents"])
                fout.write(json.dumps(example) + "\n")
                counter += 1
                if counter % 10000 == 0:
                    print("Dump {} examples".format(counter))
            elif not line:
                break
        print("Done")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--language", type=str, choices=["zh", "ar", "fr"])
    parser.add_argument("--corpus_directory", type=str)
    parser.add_argument("--output_path", type=str)
    args = parser.parse_args()

    dir = os.path.dirname(args.output_path)
    if not os.path.exists(dir):
        os.makedirs(dir)

    corpus2json(args.language, args.corpus_directory, args.output_path)

#!/usr/bin/env python
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
"""Convert official RETECO Track 1 documents from id/content to id/contents JSONL."""

import argparse
import json
from pathlib import Path

DOMAINS = ("bitcoin", "cardano", "economics", "genealogy", "history", "hsm", "iota", "law", "monero", "politics", "quant", "travel", "workplace")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--input", type=Path, required=True, help="Downloaded dataset directory containing track1_tempo/")
    parser.add_argument("--output", type=Path, required=True, help="New Anserini collection directory, e.g., collections/reteco")
    parser.add_argument("--domains", nargs="+", choices=DOMAINS, default=DOMAINS, help="Domains to convert (default: all 13)")
    args = parser.parse_args()
    source_root = args.input.resolve()
    output_root = args.output.resolve()
    if source_root == output_root or source_root in output_root.parents or output_root in source_root.parents:
        parser.error("Input and output directories must be disjoint")
    sources = [args.input / "track1_tempo" / d / "documents.jsonl" for d in args.domains]
    targets = [args.output / "track1_tempo" / d / "docs.jsonl" for d in args.domains]
    for source, target in zip(sources, targets):
        if not source.is_file():
            parser.error(f"Missing source: {source}")
        if target.exists() or target.is_symlink():
            parser.error(f"Refusing to overwrite: {target}")
    for source, target in zip(sources, targets):
        target.parent.mkdir(parents=True, exist_ok=True)
        ids = set()
        with source.open(encoding="utf-8") as src, target.open("x", encoding="utf-8") as dst:
            for line in src:
                row = json.loads(line)
                if row["id"] in ids:
                    raise ValueError(f"Duplicate ID in {source}: {row['id']}")
                ids.add(row["id"])
                dst.write(json.dumps({"id": row["id"], "contents": row["content"]}, ensure_ascii=False) + "\n")
        print(f"{target}: {len(ids)} documents")


if __name__ == "__main__":
    main()

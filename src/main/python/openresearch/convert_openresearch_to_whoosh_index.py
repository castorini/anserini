import argparse
import gzip
import json
import os
import time
from os import listdir
from os.path import isfile, join
from whoosh.index import create_in
from whoosh.fields import *


def create_dataset(args):
    print('Converting data...')

    file_names = listdir(args.collection_path)
    file_paths = []
    for file_name in file_names:
        file_path = join(args.collection_path, file_name)
        if not isfile(file_path):
            continue
        if not file_path.endswith('.gz'):
            continue
        file_paths.append(file_path)

    print('{} files found'.format(len(file_paths)))

    # We need to create whoosh index files to do the key term extraction
    schema = Schema(title=TEXT,
                    abstract=TEXT,
                    id=ID(stored=True))
    if os.path.exists(args.whoosh_index):
        assert False
    else:
        os.mkdir(args.whoosh_index)
    whoosh_index = create_in(args.whoosh_index, schema)
    writer = whoosh_index.writer()

    line_num = 0
    start_time = time.time()
    for file_num, file_path in enumerate(file_paths):
        with gzip.open(file_path) as f:
            for line in f:
                obj = json.loads(line.strip())
                doc_id = obj['id']

                writer.add_document(id=doc_id, title=obj['title'], abstract=obj['paperAbstract'])
                line_num += 1
                if line_num % 100000 == 0:
                    print("{} lines whoosh indexed in {} seconds\r".format(line_num, int(time.time()-start_time)))

    writer.commit()


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='''Converts Open Research Corpus jsonl collection to Whoosh's index.''')
    parser.add_argument('--collection_path', required=True, help='Open Research jsonl collection file')
    parser.add_argument('--whoosh_index', required=True, help='whoosh index folder')
    args = parser.parse_args()

    create_dataset(args)
    print('Done!')

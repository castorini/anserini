import argparse
import json
import os
import time
from whoosh.index import create_in
from whoosh.fields import *


def create_dataset(args):
    print('Converting data...')

    file_name = os.path.join(args.collection_path, 'corpus.json')

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
    with open(file_name) as f:
        for line in f:
            obj = json.loads(line.strip())
            doc_id = obj['id']
            writer.add_document(id=doc_id, title=obj['title'], abstract=obj['abstract'])
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

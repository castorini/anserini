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

import os
import json
import re
import bz2
import gzip
import argparse
import xml.sax
import collections
import numpy as np

YahooAnswerRecParsed = collections.namedtuple('YahooAnswerRecParsed',
                                              'uri subject content' +
                                              ' best_answ_id answ_list')

YAWNS_DOC_TAG = 'document'
YAWNS_URI_TAG = 'uri'
YAWNS_SUBJ_TAG = 'subject'
YAWNS_CONTENT_TAG = 'content'
YAWNS_BESTANSW_TAG = 'bestanswer'
YAWNS_ANSWITEM_TAG = 'answer_item'

MAX_REL_GRADE = 4


class YahooAnswersContentHandler(xml.sax.ContentHandler):
    def __init__(self, worker_obj):
        xml.sax.ContentHandler.__init__(self)
        self.worker_obj = worker_obj
        self.qty = 0

    def startElement(self, name, attrs):
        self.curr_txt = ''

        if name == YAWNS_DOC_TAG:
            self.best_answ = None
            self.uri = None
            self.subject = ''
            self.content = ''
            self.answ_list = []

    def endElement(self, name):
        if name == YAWNS_DOC_TAG:
            best_answ_id = None
            if self.best_answ is not None:
                for i in range(len(self.answ_list)):
                    if self.best_answ == self.answ_list[i]:
                        best_answ_id = i
                        break

            self.worker_obj(self.qty,
                            YahooAnswerRecParsed(self.uri,
                                                 self.subject,
                                                 self.content,
                                                 best_answ_id,
                                                 self.answ_list))
            self.qty += 1

        elif name == YAWNS_BESTANSW_TAG:
            self.best_answ = remove_tags(self.curr_txt)
        elif name == YAWNS_URI_TAG:
            self.uri = self.curr_txt
        elif name == YAWNS_ANSWITEM_TAG:
            self.answ_list.append(remove_tags(self.curr_txt))
        elif name == YAWNS_SUBJ_TAG:
            self.subject = remove_tags(self.curr_txt)
        elif name == YAWNS_CONTENT_TAG:
            self.content = remove_tags(self.curr_txt)

    def characters(self, content):
        self.curr_txt += content


def qrel_entry(quest_id, answ_id, rel_grade):
    """Produces one QREL entry

    :param quest_id:  question ID
    :param answ_id:   answer ID
    :param rel_grade: relevance grade
    :return: QREL entry
    """
    return f'{quest_id}\t0\t{answ_id}\t{rel_grade}'


def open_file(file_name, flags='r'):
    """Opens a regular or compressed file (decides on the name)

      :param  file_name a name of the file, it has a '.gz' or
              '.bz2' extension, we open a compressed stream.
      :param  flags    open flags such as 'r' or 'w'
    """
    if file_name.endswith('.gz'):
        return gzip.open(file_name, flags)
    elif file_name.endswith('.bz2'):
        return bz2.open(file_name, flags)
    else:
        return open(file_name, flags)


def remove_tags(s):
    """Just remove anything that looks like a tag"""
    return re.sub(r'</?[a-z]+\s*/?>', '', s)


def replace_tabs_nls(s):
    return re.sub(r'[\t\n\r]', ' ', s)


class Worker:

    def __init__(self, output_folder, max_docs_per_file, query_sample_qty):
        self.max_docs_per_file = max_docs_per_file
        self.output_folder = output_folder
        self.file_index = 0
        self.query_sample_qty = query_sample_qty
        self.conv_qty = 0
        self.questions = []
        self.qrels = dict()

    def __call__(self, ln, rec):

        if self.conv_qty % self.max_docs_per_file == 0:
            if self.conv_qty > 0:
                self.output_jsonl_file.close()
            output_path = os.path.join(self.output_folder,
                                       'docs{:02d}.json'.
                                       format(self.file_index))
            self.output_jsonl_file = open(output_path, 'w')
            self.file_index += 1

        question = replace_tabs_nls(rec.subject + ' ' + rec.content).strip()
        qid = rec.uri

        if len(rec.answ_list) == 0:  # Ignore questions without answers
            print('Ignoring b/c there are no answers, line id', ln)
            return
        if rec.uri is None:
            print('Ignoring b/c there is no question ID, line id', ln)
            return
        if not question:
            print('Ignoring b/c there question is empty, line id', ln)
            return

        self.questions.append((qid, question))

        self.qrels[qid] = []

        qrels = self.qrels[qid]

        for i in range(len(rec.answ_list)):
            aid = qid + '-' + str(i)
            answ = rec.answ_list[i]

            rel_grade = MAX_REL_GRADE - 1
            if rec.best_answ_id is not None and rec.best_answ_id == i:
                rel_grade += 1

            qrels.append((aid, rel_grade))

            output_dict = {'id': aid, 'contents': answ}
            self.output_jsonl_file.write(json.dumps(output_dict) + '\n')

        self.conv_qty += 1
        if self.conv_qty % 100000 == 0:
            print('Converted {} questions in {} files'.
                  format(self.conv_qty, self.file_index))

    def finish(self):
        print('Converted {} questions in {} files'.
              format(self.conv_qty, self.file_index))
        self.output_jsonl_file.close()
        # Let's sample queries and write corresponding data (queries + qrels)
        query_qty = len(self.questions)
        print('Sampling %d out of %d questions' %
              (self.query_sample_qty, query_qty))
        query_indx = np.random.choice(np.arange(query_qty),
                                      self.query_sample_qty)
        with open(os.path.join(self.output_folder, 'queries.tsv'), 'w') as f:
            for i in query_indx:
                f.write('%s\t%s\n' %
                        (self.questions[i][1],
                         self.questions[i][0]))
        with open(os.path.join(self.output_folder, 'qrels.tsv'), 'w') as f:
            for i in query_indx:
                qid = self.questions[i][0]
                for aid, rel_grade in self.qrels[qid]:
                    f.write(qrel_entry(quest_id=qid, answ_id=aid,
                                       rel_grade=rel_grade) + '\n')


def convert_collection(args):
    print('Converting collection...')

    worker = Worker(args.output_folder,
                    args.max_docs_per_file,
                    args.query_sample_qty)

    xml.sax.parse(open_file(args.collection_path),
                  YahooAnswersContentHandler(worker))

    worker.finish()


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
      description='Converts Yahoo Answers collection to Anserini jsonl files.')
    parser.add_argument('--collection_path', required=True,
                        help='Yahoo Answers file')
    parser.add_argument('--output_folder', required=True, help='output file')
    parser.add_argument('--random_seed', default=0, type=float,
                        help='random seed')
    parser.add_argument('--query_sample_qty', type=int, required=True,
                        help='# of queries to sample')
    parser.add_argument('--max_docs_per_file', default=1000000, type=int,
                        help='maximum number of documents in each jsonl file.')

    args = parser.parse_args()

    np.random.seed(args.random_seed)

    if not os.path.exists(args.output_folder):
        os.makedirs(args.output_folder)

    convert_collection(args)
    print('Done!')

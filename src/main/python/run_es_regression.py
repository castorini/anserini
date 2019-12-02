# -*- coding: utf-8 -*-
'''
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
'''

import argparse
import logging
import math
import os
import requests
import time

from subprocess import call, Popen, PIPE

# Note that this class is specifically written with REST API requests instead of the
# Elasticsearch client eliminate an additional dependency

logger = logging.getLogger('run_es_regression')
ch = logging.StreamHandler()
ch.setFormatter(logging.Formatter('%(asctime)s %(levelname)s - %(message)s'))
logger.addHandler(ch)
logger.setLevel(logging.INFO)

class ElasticsearchClient:
    def __init__(self):
        pass

    def run_shell_command(self, command):
        process = Popen(command, shell=True, stdout=PIPE)
        output, err = process.communicate()
        if process.returncode == 0: # success
            return output.decode('utf8')
        else:
            raise RuntimeError('Error running shell comand: {}'.format(command))

    def is_alive(self):
        try:
            response = requests.get('http://localhost:9200/')
            response.raise_for_status()
        except:
            return False
        else:
            return True

    def does_index_exist(self, collection):
        # Make sure ES is alive:
        if self.is_alive():
            try:
                response = requests.get('http://localhost:9200/{}'.format(collection))
                response.raise_for_status()
            except:
                return False
            else:
                return True
        else:
            raise Exception('ES does not appear to be alive!')

    def delete_index(self, collection):
        logger.info('Deleting index {}...'.format(collection))
        # Make sure the index exists:
        if self.does_index_exist(collection):
            try:
                response = requests.request('DELETE', url='http://localhost:9200/{}'.format(collection))
                response.raise_for_status()
            except:
                return False
            else:
                return True
        else:
            raise Exception('The index {} does not exist!'.format(collection))

    def create_index(self, collection):
        logger.info('Creating index {}...'.format(collection))
        # Make sure the index does not exist:
        if not self.does_index_exist(collection):
            filename = 'src/main/resources/elasticsearch/index-config.{}.json'.format(collection)
            if not os.path.exists(filename):
                raise Exception('No config found in src/main/resources/elasticsearch/ for {}!'.format(collection))
            logger.info('Using index config for {} at {}'.format(collection, filename))
            file = open(filename, mode='r')
            json = file.read()
            file.close()
            try:
                response = requests.request('PUT', url='http://localhost:9200/{}'.format(collection),
                                            data=json, headers={'Content-type': 'application/json'})
                response.raise_for_status()
            except:
                logger.info(response)
                return False
            else:
                return True
        else:
            raise Exception('The index {} already exists!'.format(collection))

    def insert_docs(self, collection, path):
        logger.info('Inserting documents from {} into {}... '.format(path, collection))
        if not self.does_index_exist(collection):
            raise Exception('The index {} does not exist!'.format(collection))
        # TODO: abstract this into an external config instead of hard-coded.
        command = ''
        if collection == 'robust04':
            command = 'sh target/appassembler/bin/IndexCollection -collection TrecCollection ' + \
                      '-generator JsoupGenerator -es -es.index robust04 -threads 16 -input ' + \
                      path + ' -storePositions -storeDocvectors -storeRawDocs'
        else:
            raise Exception('Unknown collection: {}'.format(collection))
        logger.info('Running indexing command: ' + command)
        return self.run_shell_command(command)

    def evaluate(self, collection):
        # TODO: abstract this into an external config instead of hard-coded.
        command = ''
        if collection == 'robust04':
            command = 'sh target/appassembler/bin/SearchElastic -topicreader Trec -es.index robust04 ' + \
                      '-topics src/main/resources/topics-and-qrels/topics.robust04.txt ' + \
                      '-output run.es.robust04.bm25.topics.robust04.txt'
        else:
            raise Exception('Unknown collection: {}'.format(collection))
        logger.info('Retrieval command: ' + command)
        output = self.run_shell_command(command)
        logger.info('Retrieval complete!')

        if collection == 'robust04':
            command = 'eval/trec_eval.9.0.4/trec_eval -m map -m P.30 ' + \
                      'src/main/resources/topics-and-qrels/qrels.robust04.txt run.es.robust04.bm25.topics.robust04.txt'
        else:
            raise Exception('Unknown collection: {}'.format(collection))

        logger.info('Evaluation command: ' + command)
        output = self.run_shell_command(command)
        ap = float(output.split('\n')[0].split('\t')[2])

        if collection == 'robust04':
            if math.isclose(ap, 0.2531):
                logger.info('[SUCESS] {} MAP verified as expected!'.format(ap))
            else:
                logger.info('[FAIL] {} MAP, 0.2531 MAP expected!'.format(ap))
        else:
            raise Exception('Unknown collection: {}'.format(collection))



if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Program for running Elasticsearch regressions.')
    parser.add_argument('--ping', action='store_true', default=False, help='ping ES and exit')
    parser.add_argument('--check-index-exists', default='', type=str, metavar='collection', help='check if index exists')
    parser.add_argument('--delete-index', default='', type=str, metavar='collection', help='check if index exists')
    parser.add_argument('--create-index', default='', type=str, metavar='collection', help='creates index')
    parser.add_argument('--insert-docs', default='', type=str, metavar='collection', help='insert documents into index')
    parser.add_argument('--input', default='', type=str, metavar='directory', help='location of documents to insert into index')
    parser.add_argument('--evaluate', default='', type=str, metavar='collection', help='search and evaluate on collection')
    parser.add_argument('--regression', default='', type=str, metavar='collection', help='run end-to-end regression')

    args = parser.parse_args()
    es = ElasticsearchClient()

    if args.ping:
        logger.info('Pinging Elasticsearch instance...')
        if es.is_alive():
            logger.info('... appears to alive! :)')
        else:
            logger.info('... appears to dead! :(')
    elif args.check_index_exists:
        logger.info('Checking if index {} exists...'.format(args.check_index_exists))
        if es.does_index_exist(args.check_index_exists):
            logger.info('... yes indeed!')
        else:
            logger.info('... appears not.')
    elif args.delete_index:
        if es.delete_index(args.delete_index):
            logger.info('... successful!')
        else:
            logger.info('... failed!')
    elif args.create_index:
        if es.create_index(args.create_index):
            logger.info('... successful!')
        else:
            logger.info('... failed!')
    elif args.insert_docs:
        if not args.input:
            raise Exception('Location of corpus not specified (use --input)!')
        else:
            es.insert_docs(args.insert_docs, args.input)
    elif args.evaluate:
        es.evaluate(args.evaluate)
    elif args.regression:
        logger.info('Running BM25 regression on {}...'.format(args.regression))
        if not args.input:
            raise Exception('Location of corpus not specified (use --input)!')
        if not es.is_alive():
            raise Exception('Elasticsearch does not appear to be alive!')
        if es.does_index_exist(args.regression):
            logger.info('Index {} already exists: deleting and recreating.'.format(args.regression))
            es.delete_index(args.regression)
        es.create_index(args.regression)
        es.insert_docs(args.regression, args.input)
        logger.info('Document ingestion complete. Sleeping now for 60s...')
        time.sleep(60)
        logger.info('Waking up!')
        es.evaluate(args.regression)

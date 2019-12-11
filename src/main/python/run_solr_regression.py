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
import regression_utils

logger = logging.getLogger('run_solr_regression')
ch = logging.StreamHandler()
ch.setFormatter(logging.Formatter('%(asctime)s %(levelname)s - %(message)s'))
logger.addHandler(ch)
logger.setLevel(logging.INFO)

class SolrClient:
    def __init__(self):
        pass

    def is_alive(self):
        try:
            response = requests.get('http://localhost:8983/')
            response.raise_for_status()
        except: return False
        else: return True

    def does_index_exist(self, collection):
        # Make sure Solr is alive:
        if self.is_alive():
            try:
                response = requests.get('http://localhost:8983/solr/admin/collections?action=LIST')
                response.raise_for_status()
            except: return False
            else:
                return collection in response.json()['collections']
        else: raise Exception('Solr does not appear to be alive!')

    def delete_index(self, collection):
        # Make sure the index exists:
        if self.does_index_exist(collection):
            command = 'solrini/bin/solr delete -c {}'.format(collection)
            logger.info('Deleting index {} command: {}'.format(collection, command))
            output = regression_utils.run_shell_command(command, logger, echo=True)
            return not self.does_index_exist(collection)
        else: raise Exception('The index {} does not exist!'.format(collection))

    def create_index(self, collection):
        # Make sure the index does not exist:
        if not self.does_index_exist(collection):
            # Re-upload configsets to Solr's internal Zookeeper
            self.upload_configs()
            command = 'solrini/bin/solr create -n anserini -c {}'.format(collection)
            logger.info('Creating index {} command: {}'.format(collection, command))
            output = regression_utils.run_shell_command(command, logger, echo=True)
            return self.does_index_exist(collection)
        else:
            raise Exception('The index {} already exists!'.format(collection))

    def insert_docs(self, collection, path):
        logger.info('Inserting documents from {} into {}... '.format(path, collection))
        if not os.path.exists(args.input):
            raise Exception('{} does not exist!'.format(args.input))
        if not self.does_index_exist(collection):
            raise Exception('The index {} does not exist!'.format(collection))
        command = ''
        if collection == 'core18':
            command = 'sh target/appassembler/bin/IndexCollection -collection WashingtonPostCollection ' + \
                      '-generator WapoGenerator -solr -solr.index core18 -solr.zkUrl localhost:9983 ' + \
                      '-threads 8 -input ' + path + ' -storePositions -storeDocvectors -storeTransformedDocs'
        else:
            raise Exception('Unknown collection: {}'.format(collection))
        logger.info('Running indexing command: ' + command)
        return regression_utils.run_shell_command(command, logger, echo=True)

    def upload_configs(self):
        os.chdir('src/main/resources/solr')
        command = 'rm -rf anserini/conf/lang anserini-twitter/conf/lang'
        logger.info('Deleting existed configs command: ' + command)
        output = regression_utils.run_shell_command(command, logger, echo=True)
        command = './solr.sh ../../../../solrini localhost:9983'
        logger.info('Uploading configs command: ' + command)
        output = regression_utils.run_shell_command(command, logger, echo=True)
        os.chdir('../../../..')
        logger.info('Uploading complete!')

    def evaluate(self, collection):
        if not self.does_index_exist(collection):
            raise Exception('The index {} does not exist!'.format(collection))
        command = ''
        if collection == 'core18':
            command = 'sh target/appassembler/bin/SearchSolr -topicreader Trec -solr.index core18 ' + \
                      '-solr.zkUrl localhost:9983 -topics src/main/resources/topics-and-qrels/topics.core18.txt ' + \
                      '-output run.solr.core18.bm25.topics.core18.txt'
        else:
            raise Exception('Unknown collection: {}'.format(collection))

        logger.info('Retrieval command: ' + command)
        output = regression_utils.run_shell_command(command, logger, echo=True)
        logger.info('Retrieval complete!')

        if collection == 'core18':
            command = 'eval/trec_eval.9.0.4/trec_eval -m map -m P.30 ' + \
                      'src/main/resources/topics-and-qrels/qrels.core18.txt run.solr.core18.bm25.topics.core18.txt'
        else:
            raise Exception('Unknown collection: {}'.format(collection))

        logger.info('Evaluation command: ' + command)
        output = regression_utils.run_shell_command(command, logger, capture=True)
        ap = float(output[0].split('\t')[2])

        expected = 0
        if collection == 'core18': expected = 0.2495
        else: raise Exception('Unknown collection: {}'.format(collection))

        if math.isclose(ap, expected): logger.info('[SUCESS] {} MAP verified as expected!'.format(ap))
        else: logger.info('[FAILED] {} MAP, expected {} MAP!'.format(ap, expected))



if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Program for running Solr regressions.')
    parser.add_argument('--ping', action='store_true', default=False, help='ping Solr and exit')
    parser.add_argument('--check-index-exists', default='', type=str, metavar='collection', help='check if index exists')
    parser.add_argument('--delete-index', default='', type=str, metavar='collection', help='deletes index')
    parser.add_argument('--create-index', default='', type=str, metavar='collection', help='creates index')
    parser.add_argument('--insert-docs', default='', type=str, metavar='collection', help='insert documents into index')
    parser.add_argument('--input', default='', type=str, metavar='directory', help='location of documents to insert into index')
    parser.add_argument('--evaluate', default='', type=str, metavar='collection', help='search and evaluate on collection')
    parser.add_argument('--regression', default='', type=str, metavar='collection', help='run end-to-end regression')

    args = parser.parse_args()
    solr = SolrClient()

    if args.ping:
        logger.info('Pinging Solr instance...')
        if solr.is_alive():
            logger.info('... appears to alive! :)')
        else:
            logger.info('... appears to dead! :(')
    elif args.check_index_exists:
        logger.info('Checking if index {} exists...'.format(args.check_index_exists))
        if solr.does_index_exist(args.check_index_exists):
            logger.info('... yes indeed!')
        else:
            logger.info('... appears not.')
    elif args.delete_index:
        if solr.delete_index(args.delete_index):
            logger.info('... successful!')
        else:
            logger.info('... failed!')
    elif args.create_index:
        if solr.create_index(args.create_index):
            logger.info('... successful!')
        else:
            logger.info('... failed!')
    elif args.insert_docs:
        if not args.input:
            raise Exception('Location of corpus not specified (use --input)!')
        else:
            solr.insert_docs(args.insert_docs, args.input)
    elif args.evaluate:
        solr.evaluate(args.evaluate)
    elif args.regression:
        logger.info('Running BM25 regression on {}...'.format(args.regression))
        if not args.input:
            raise Exception('Location of corpus not specified (use --input)!')
        if not solr.is_alive():
            raise Exception('Solr does not appear to be alive!')
        if solr.does_index_exist(args.regression):
            logger.info('Index {} already exists: deleting and recreating.'.format(args.regression))
            solr.delete_index(args.regression)
        solr.create_index(args.regression)
        solr.insert_docs(args.regression, args.input)
        solr.evaluate(args.regression)

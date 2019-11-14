"""
This module generates LTR features for MSMARCO dataset.
Command line:
python generate_ltr_features.py --index lucene-index.msmarco-doc.pos+docvectors+rawdocs \
 --queries src/main/resources/topics-and-qrels/topics.msmarco-doc.dev.txt \
 --output ltr-features.csv

Creation Date: 11/14/2019
Last Modified: 11/14/2019
Author: Kamyar Ghajar <k.ghajar@gmail.com>
"""
import argparse
import time
from pyserini.search import pysearch


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Generate MSMARCO learn to rank features.')
    parser.add_argument('--queries', required=True, default='', help='query (id<space>string) mapping file')
    parser.add_argument('--output', required=True, default='', help='output file')
    parser.add_argument('--index', required=True, default='', help='index path')
    parser.add_argument('--hits', default=10, help='number of hits to retrieve')
    parser.add_argument('--k1', default=0.82, help='BM25 k1 parameter')
    parser.add_argument('--b', default=0.68, help='BM25 b parameter')
    parser.add_argument('--threads', default=1, type=int, help='Maximum number of threads')
    
    args = parser.parse_args()
    
    total_start_time = time.time()
    
    searcher = pysearch.SimpleSearcher(args.index)
    searcher.set_bm25_similarity(float(args.k1), float(args.b))
    print('Initializing BM25, setting k1={} and b={}'.format(args.k1, args.b), flush=True)
    
    if args.threads == 1:
        
        with open(args.output, 'w') as fout:
            start_time = time.time()
            for line_number, line in enumerate(open(args.queries)):
                qid, query = line.strip().split('\t')
                query_terms_count = len(query.split())
                hits = searcher.search(query.encode('utf8'), int(args.hits))
                if line_number % 100 == 0:
                    time_per_query = (time.time() - start_time) / (line_number + 1)
                    print('Retrieving query {} ({:0.3f} s/query)'.format(line_number, time_per_query), flush=True)
                for rank in range(len(hits)):
                    docno = hits[rank].docid
                    bm25_score = hits[rank].score
                    fout.write('{}\t{}\t{}\t{}\t{}\n'.format(qid, docno, bm25_score, query_terms_count, rank + 1))
    else:
        qids = []
        queries = []
        qid_term_count_dict = dict()
        
        for line_number, line in enumerate(open(args.queries)):
            qid, query = line.strip().split('\t')
            qids.append(qid)
            queries.append(query)
            qid_term_count_dict[qid] = len(query.split())
        
        results = searcher.batch_search(queries, qids, args.hits, -1, args.threads)
        
        with open(args.output, 'w') as fout:
            for qid in qids:
                hits = results.get(qid)
                query_term_count = qid_term_count_dict[qid]
                for rank in range(len(hits)):
                    docno = hits[rank].docid
                    bm25_score = hits[rank].score
                    fout.write('{}\t{}\t{}\t{}\t{}\n'.format(qid, docno, bm25_score, query_term_count, rank + 1))
    
    total_time = (time.time() - total_start_time)
    print('Total feature extraction time: {:0.3f} s'.format(total_time))
    print('Done!')

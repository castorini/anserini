import argparse
from collection_iterator import *
import logging
    
if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG,
                        filename='json_segment.log',
                        format='%(asctime)s %(name)s %(threadName)s %(levelname)s - %(message)s', 
                        datefmt='%m/%d/%Y %I:%M:%S ')

    parser = argparse.ArgumentParser()
    parser.add_argument("--input", '-i', type=str,
                        help='path to input directory containing collection', required=True)
    parser.add_argument("--collection", '-c', type=str,
                    help='collection class', required=True)
    parser.add_argument("--generator", '-g', type=str,
                    help='generator class', required=True)
    parser.add_argument("--output", '-o', type=str,
                    help='path to create output directory', required=True)
    parser.add_argument("--threads", '-th', type=int, default=1,
                    help='number of threads', required=False)
    parser.add_argument("--tokenize", '-t', type=str, default=None,
                    help='defaults to full documents if tokenizer not provided.', required=False)
    parser.add_argument("--raw", '-r', action='store_true',
                    help='defaults to false for transformed text', required=False)

    args = parser.parse_args()
    
    # iterating over collection, tokenize (opt.), and 
    # write JsonCollection to output
    IterCollection(args.input, args.collection, args.generator, args.output, 
                   args.threads, args.tokenize, args.raw)





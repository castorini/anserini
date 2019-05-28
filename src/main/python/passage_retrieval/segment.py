import argparse
import os
import logging

from collection_iterator import IterSegment, IterCollection


if __name__ == '__main__':

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
    parser.add_argument("--min", '-m', type=int, default=0,
                    help='minimum size value for tokenizer, defaults to zero', required=False)
    parser.add_argument("--raw", '-r', action='store_true',
                    help='defaults to false for transformed text', required=False)

    args = parser.parse_args()
    
    logfile = os.path.join(os.path.dirname(os.path.abspath(args.output)), 
                           os.path.basename(os.path.abspath(args.output)) + '.log')
    
    logging.basicConfig(level=logging.DEBUG,
                        filename=logfile,
                        format='%(asctime)s %(name)s %(threadName)s %(levelname)s - %(message)s', 
                        datefmt='%m/%d/%Y %I:%M:%S ')
    
    # iterating over collection, tokenize (opt.), and 
    # write JsonCollection to output
    IterCollection(args.input, args.collection, args.generator, args.output, 
                   args.threads, args.tokenize, args.min, args.raw)

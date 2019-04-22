import json
import shlex
import subprocess
import sys
import string
import time
from absl import flags
import jnius_config
jnius_config.set_classpath("/home/rfn216/Anserini/target/anserini-0.3.1-SNAPSHOT-fatjar.jar")

from jnius import autoclass
JString = autoclass('java.lang.String')
JSearcher = autoclass('io.anserini.search.SimpleSearcher')

FLAGS = flags.FLAGS

flags.DEFINE_string("qid_queries", None, "query id - query mapping file.")
flags.DEFINE_string("output", None, "File to write the retrieved docs.")
flags.DEFINE_string("index", None, "Path to the Anserini Index.")
flags.DEFINE_integer("hits", 10, "File containing the topics.")

FLAGS(sys.argv)

searcher = JSearcher(JString(FLAGS.index))
searcher.setBM25Similarity(0.9, 0.4)

with open(FLAGS.output, "w") as fout:
  start_time = time.time()
  for line_number, line in enumerate(open(FLAGS.qid_queries)):
    qid, query = line.strip().split("\t")
    hits = searcher.search(JString(query.encode("utf8")), FLAGS.hits)
    if line_number % 10 == 0:
      time_per_query = (time.time() - start_time) / (line_number + 1)
      print("Retrieving query {} ({:0.3f} s/query)".format(
          line_number, time_per_query))
    for rank in range(len(hits)):
      docno = hits[rank].docid
      fout.write("{}\t{}\t{}\n".format(qid, docno, rank + 1))

print('Done!')



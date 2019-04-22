'''Keeps only queries that are in the qrels file.'''
from absl import app
from absl import flags


FLAGS = flags.FLAGS

flags.DEFINE_string('qrels', None, 'MS MARCO .tsv qrels file.')
flags.DEFINE_string('queries', None, 'queries file.')
flags.DEFINE_string('output_queries', None, 'Path to write the queries file.')


def main(_):
  qrels = set()
  with open(FLAGS.qrels) as f:
    for line in f:
      query_id, _, _, _ = line.rstrip().split('\t')
      qrels.add(query_id)

  with open(FLAGS.output_queries, 'w') as fout:
    with open(FLAGS.queries) as f:
      for line in f:
        query_id, _ = line.rstrip().split('\t')
        if query_id in qrels:
          fout.write(line)

  print('Done!')


if __name__ == '__main__':
  flags.mark_flag_as_required('qrels')
  flags.mark_flag_as_required('queries')
  flags.mark_flag_as_required('output_queries')
  app.run(main)

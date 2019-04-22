'''Converts MSMARCO's tsv collection to Anserini jsonl files.'''
import json
import os
from absl import app
from absl import flags


FLAGS = flags.FLAGS

flags.DEFINE_string('collection_path', None, 
                    'MS MARCO .tsv collection file.')
flags.DEFINE_string('output_folder', None, 'Folder to write the jsonl files.')
flags.DEFINE_integer('max_docs_per_file', 1000000, 
                     'Maximum number of documents in each jsonl file.')


def convert_collection():
  print('Converting collection...')
  file_index = 0
  with open(FLAGS.collection_path) as f:
    for i, line in enumerate(f):
      doc_id, doc_text = line.rstrip().split('\t')

      if i % FLAGS.max_docs_per_file == 0:
        if i > 0:
          output_jsonl_file.close()
        output_path = os.path.join(
            FLAGS.output_folder, 'docs{:02d}.json'.format(file_index))
        output_jsonl_file = open(output_path, 'w')
        file_index += 1
      output_dict = {'id': doc_id, 'contents': doc_text}
      output_jsonl_file.write(json.dumps(output_dict) + '\n')

      if i % 100000 == 0:
        print('Converted {} docs in {} files'.format(i, file_index))

  output_jsonl_file.close()


def main(_):
  if not os.path.exists(FLAGS.output_folder):
    os.makedirs(FLAGS.output_folder)

  convert_collection()
  print('Done!')


if __name__ == '__main__':
  flags.mark_flag_as_required('collection_path')
  flags.mark_flag_as_required('output_folder')
  app.run(main)

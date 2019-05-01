'''Converts MSMARCO's tsv collection to Anserini jsonl files.'''
import json
import os
from absl import app
from absl import flags


FLAGS = flags.FLAGS

flags.DEFINE_string('collection_path', None, 
                    'MS MARCO .tsv collection file.')
flags.DEFINE_string('predictions', None, 
                    'Query Predictions that will be attached to documents.')
flags.DEFINE_string('output_folder', None, 'Folder to write the jsonl files.')
flags.DEFINE_integer('beam_size', None, 
                    'Number of predictions made per document in the predictions file.')
flags.DEFINE_integer('max_docs_per_file', 1000000, 
                     'Maximum number of documents in each jsonl file.')


def convert_collection():
  print('Converting collection...')

  predictions_file = open(FLAGS.predictions)
  file_index = 0
  with open(FLAGS.collection_path) as f:
    for i, line in enumerate(f):

      # Start writting to a new file whent the current one reached its maximum 
      # capacity. 
      if i % FLAGS.max_docs_per_file == 0:
        if i > 0:
          output_jsonl_file.close()
        output_path = os.path.join(
            FLAGS.output_folder, 'docs{:02d}.json'.format(file_index))
        output_jsonl_file = open(output_path, 'w')
        file_index += 1

      doc_id, doc_text = line.rstrip().split('\t')

      # Reads from predictions and merge then to the original doc text. 
      pred_text = []
      for _ in range(FLAGS.beam_size):
        pred_text.append(predictions_file.readline().strip())
      pred_text = ' '.join(pred_text)
      pred_text = pred_text.replace(' / ', ' ')
      text = doc_text + ' ' + pred_text
      
      output_dict = {'id': doc_id, 'contents': text}
      output_jsonl_file.write(json.dumps(output_dict) + '\n')

      if i % 100000 == 0:
        print('Converted {} docs in {} files'.format(i, file_index))

  output_jsonl_file.close()
  predictions_file.close()


def main(_):
  if not os.path.exists(FLAGS.output_folder):
    os.makedirs(FLAGS.output_folder)

  convert_collection()
  print('Done!')


if __name__ == '__main__':
  flags.mark_flag_as_required('collection_path')
  flags.mark_flag_as_required('predictions')
  flags.mark_flag_as_required('output_folder')
  flags.mark_flag_as_required('beam_size')
  app.run(main)

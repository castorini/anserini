import os
import argparse

TOPIC_LIST = {
    '307', '310', '321', '325', '330', '336', '341', '344', '345', '347', '350',
    '353', '354', '355', '356', '362', '363', '367', '372', '375', '378', '379',
    '389', '393', '394', '397', '399', '400', '404', '408', '414', '416', '419',
    '422', '423', '426', '427', '433', '435', '436', '439', '442', '443', '445',
    '614', '620', '626', '646', '677', '690'
}

if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  parser.add_argument(
      '--input',
      '-I',
      type=str,
      help='rerank results to be filtered',
      required=True)
  parser.add_argument(
      '--output', '-O', type=str, help='the output file path', required=True)
  args = parser.parse_args()
  input_path = args.input
  output_path = args.output

  with open(input_path, 'r') as fin, open(output_path, 'w') as fout:
    for line in fin:
      topic = line.split()[0]
      if topic in TOPIC_LIST:
        fout.write(line)

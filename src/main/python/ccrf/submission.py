import argparse

topic_list = [
    '321', '336', '341',
    '347', '350', '362',
    '363', '367', '375', '378', '393',
    '397', '400', '408', '414',
    '422', '426', '427', '433',
    '439', '442', '445', '626', '646',
    '690'
]

def submission(origin_file, runtag, output_file):
  with open(output_file, 'a') as fout, open(origin_file, 'r') as fin:
    for line in fin:
      data = line.strip().split(' ')
      if data[0] in topic_list:
        continue
      data[-1] = runtag
      fout.write(' '.join(data) + '\n')

def ensemble(folder, ratio, num_ensemble, runtag, output):
  ensemble_dict = {}
  if num_ensemble == 1:
    clf_list = ['LR2']
  elif num_ensemble == 3:
    clf_list = ['LR2', 'SVM', 'LGB']
  elif num_ensemble == 7:
    clf_list = ['LR1', 'LR2', 'SVM', 'Ridge', 'LGB', 'SGDC', 'SGDR']
  else:
    return

  for clf in clf_list:
    with open('{}/{}/rerank_{}.txt'.format(folder, clf, ratio), 'r') as f:
      for line in f:
        data = line.split()
        topic, docid, score = data[0], data[2], float(data[4])
        if topic not in ensemble_dict:
          ensemble_dict[topic] = {}
        if docid not in ensemble_dict[topic]:
          ensemble_dict[topic][docid] = 0
        ensemble_dict[topic][docid] += score

  with open(output, 'w') as f:
    for topic in ensemble_dict:
      for rank, (docid, score) in enumerate(sorted(ensemble_dict[topic].items(),
                                                   key=lambda x: -x[1])):
        f.write('{} Q0 {} {} {} {}\n'.format(topic, docid, rank+1, score, runtag))

if __name__ == '__main__':
  parser = argparse.ArgumentParser()
  parser.add_argument("--rank-file", type=str,
                      help='path to original rerank file', required=True)
  parser.add_argument("--clf-folder", type=str,
                      help='classifier folder', required=True)
  parser.add_argument("--ratio", type=float,
                      help='interpolating ratio', required=True)
  parser.add_argument("--ensemble", type=int,
                      help='number of ensemble classifiers, '
                      'choose from 1, 3, and 7. 1 for LR2, 3 for LR2+SVM+LGB, '
                      '7 for all classifiers', required=True)
  parser.add_argument("--runtag", type=str,
                      help='submission runtag', required=True)
  parser.add_argument("--output", type=str,
                      help='output file name', required=True)

  args = parser.parse_args()
  if args.ensemble not in [1,3,7]:
    raise ValueError('Unsupported number of ensemble classifiers. Must choose from 1, 3, and 7.')
  
  ensemble(args.clf_folder, args.ratio, args.ensemble, args.runtag, args.output)
  submission(args.rank_file, args.runtag, args.output)

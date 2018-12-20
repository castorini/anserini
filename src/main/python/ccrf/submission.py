import argparse
import logging
import json
import os


def submission(origin_file, topics, runtag, output_file):
    with open(output_file, 'a') as fout, open(origin_file, 'r') as fin:
        for line in fin:
            data = line.strip().split(' ')
            if data[0] in topics:
                continue
            data[-1] = runtag
            fout.write(' '.join(data) + '\n')


def ensemble(folder, ratio, clf_list, runtag, output):
    ensemble_dict = {}
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
                f.write('{} Q0 {} {} {} {}\n'.format(topic, docid, rank + 1, score, runtag))


if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG, format='%(asctime)s %(message)s')

    parser = argparse.ArgumentParser()
    parser.add_argument("--config", type=str, help='config file', required=True)
    args = parser.parse_args()
    config_file = args.config

    # Load configuration
    with open(config_file) as f:
        config = json.load(f)

    model_directory = os.path.join(config['working_directory'], 'models')
    assert os.path.isdir(model_directory)

    for run in config['runs']:
        runtag = run['runtag']
        weight = run['weight']
        output = os.path.join(config['working_directory'], run['output'])

        logging.info(f'Preparing run for {runtag}')
        ensemble(model_directory, weight, run['classifiers'], runtag, output)
        submission(config['target']['run'], config['topics'], runtag, output)

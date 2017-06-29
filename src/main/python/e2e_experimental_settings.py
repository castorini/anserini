# The following 5 conditions vary
# idf_source, stopwords_and_stemming, punctuation, words-with-hyphens
import argparse
import itertools
import shlex
import subprocess

class Setting(object):
    def __init__(self, label, value_flag_map):
        self.label = label
        self.choice_flags = value_flag_map
    
    def get_settings(self):
        return self.choice_flags.keys()

    def get_choice(self, setting):
        return self.choice_flags[setting]

    def get_options(self):
        options = []
        for key in self.choice_flags.keys():
            options.append("{}:{}".format(self.label, key))
        return options

class Experiments(object):
    def __init__(self, qa_data_xml, qrel, word_embeddings_file, index_path, model_file):
        self.settings = {}
        self.combinations = []
        self.qa_data_xml = qa_data_xml
        self.qrel = qrel
        self.w2v_cache = word_embeddings_file
        self.index_path = index_path
        self.model_file = model_file
        self.cmd_root = "python src/main/python/run_trecqa.py -input {} -output e2e.run -qrel {} -index {} -w2v-cache {} -qa-model-file {} -model idf".\
            format(self.qa_data_xml, self.qrel, self.index_path, self.w2v_cache, self.model_file)
        self.eval_cmd_root = "./eval/trec_eval.9.0/trec_eval -m map -m recip_rank -m bpref {}".format(self.qrel)
        self.rbp_cmd_root = "rbp_eval/rbp_eval {}".format(self.qrel)

    def add_setting(self, setting):
        self.settings[setting.label] = setting
        self._setup_combinations()


    def _setup_combinations(self):
        all_settings = []
        for setting in self.settings.values():
            all_settings.append(setting.get_options())

        self.combinations = []
        for c in itertools.product(*all_settings):
             self.combinations.append(c)

    def _run_cmd(self, cmd):
        pargs = shlex.split(cmd)
        p = subprocess.Popen(pargs, stdout=subprocess.PIPE, stderr=subprocess.PIPE, \
                                bufsize=1, universal_newlines=True)
        pout, perr = p.communicate()
        return pout, perr
    
    def _run_eval(self, params):
    
        cmd = '{} e2e.run'.format(self.eval_cmd_root)
        out, err = self._run_cmd(cmd)
        # trec_eval scores
        metrics = []
        scores = []
        for line in str(out).split('\n'):
            if not line.strip().split(): continue
            fields = line.strip().split()
            metrics.append(fields[0])
            scores.append(fields[-1])
        
        # rbp_eval scores 
        cmd = '{} e2e.run'.format(self.rbp_cmd_root)
        out, err = self._run_cmd(cmd)
        for line in str(out).split('\n'):
            if not line.startswith('p= 0.50'): continue
            metrics.append('rbp_p0.5')
            scores.append(' '.join(line.strip().split()[-2:]))
        param, setting = zip(*params)
        print('{}\t{}'.format('\t'.join(param), '\t'.join(metrics)))
        print('{}\t{}'.format('\t'.join(setting), '\t'.join(scores)))


    def run(self, indices):
        """
        runs a particular combination of settings
        """
        for ci in indices:
            combo = self.combinations[ci]
            print(combo)
            params = []
            cmd_args = []
            for setting_choice in combo:
                setting, choice = setting_choice.split(':')
                cmd_args.append(self.settings[setting].choice_flags[choice])
                params.append((setting, choice))
            
            cmd = '{} {}'.format(self.cmd_root, ' '.join(cmd_args))
            print(cmd)
            out, err = self._run_cmd(cmd)
            with open('e2e.log', 'a') as lf:
                print('combination {}'.format(ci), file=lf)
                print('---------- OUT ------------', file=lf)
                print(out, file=lf)
                print('---------- ERR ------------', file=lf)
                print(err, file=lf)
            self._run_eval(params)

      
    def run_all(self):
        """
        runs all experiments
        """
        self.run([ci for ci in range(len(self.combinations))])
            
    
    def list_settings(self):
        """
        lists all settings
        """
        for c in enumerate(self.combinations):
            print(c)
        print("--run X Y Z to run combinations number X Y Z")


if __name__ == "__main__":
    ap = argparse.ArgumentParser(description="Lists exerimental settings and runs experiments")
    ap.add_argument("--list", help="lists all available experimental settings combinations",
                    action="store_true")
    ap.add_argument("--run", help='runs experimenal setting combination NUMBER(s)',
                    nargs="+", type=int)
    ap.add_argument("--run-all", action="store_true")
    ap.add_argument('qa_data_xml', help="path to the QA dataset XML file")
    ap.add_argument('qrel_file', help="the qrel file e.g. ../data/TrecQA/raw-dev.qrel")
    ap.add_argument("index_path", help="required for some combination of experiments")
    ap.add_argument('word_embeddings_file', help="the word embeddings file")
    ap.add_argument('model_file', help="the model to be used for QA similarity")
    #ap.add_argument("--idf-source", choices=["qa-data", "corpus-index"], default="corpus-index")
    #ap.add_argument("--punctuation", choices=["keep", "remove"], default="keep")
    #ap.add_argument("--dash-words", choices=["keep", "split"], default="keep")
    #ap.add_argument("models_root", help="path to folder containing models")

    args = ap.parse_args()

    experiments = Experiments(args.qa_data_xml, args.qrel_file, args.word_embeddings_file,
                              args.index_path, args.model_file)

    # experiments.add_setting(Setting('idf_source', {
    #     'qa-data': '--idf-source qa-data',
    #     'corpus-index': '--idf-source corpus-index'
    # }))

    # experiments.add_setting(Setting('punctuation', {
    #     'keep': '--punctuation keep',
    #     'remove': '--punctuation remove'
    # }))

    # experiments.add_setting(Setting('dash_words', {
    #     'keep': '--dash-words keep',
    #     'split': '--dash-words split'
    # }))

    experiments.add_setting(Setting('num_hits', {
        '100': '-hits 100',
	'200': '-hits 200',
	'300': '-hits 300',
	'400': '-hits 400',
	'500': '-hits 500',
	'600': '-hits 600',
	'700': '-hits 700',
	'800': '-hits 800',
	'900': '-hits 900',
	'1000': '-hits 1000'
    }))

    experiments.add_setting(Setting('top_k', {
       	'5': '-k 5',
	'10': '-k 10',
    }))

    experiments.list_settings()

    if args.run:
        experiments.run(args.run)

    if args.run_all:
        experiments.run_all()



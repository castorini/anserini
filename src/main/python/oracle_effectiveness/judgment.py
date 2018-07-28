import sys,os
import ast


class Judgment():
    """
    Get the judgments of a corpus.
    When constructing, pass the path of the corpus. For example, "../wt2g/"
    """
    def __init__(self, path):
        self.corpus_path = os.path.abspath(path)
        if not os.path.exists(self.corpus_path):
            print '[Judgment Constructor]:Please provide a valid corpus path'
            exit(1)

        self.judgment_file_path = os.path.join(self.corpus_path, 'judgement_file')
        if not os.path.exists(self.judgment_file_path):
            print """No judgment file found! 
                judgment file should be called "judgment_file" under 
                corpus path. You can create a symlink for it"""
            exit(1)


    def go_through_judgment_file(self, callback, callback_paras):
        """
        Check the judgment file line by line.
        A callback function processes with each line.
        callback_paras are the parameters pass to callback function
        """

        with open(self.judgment_file_path) as f:
            for line in f:
                line = line.strip()
                if line:
                    row = line.split()
                    _qid = row[0]
                    doc_id = row[2]
                    relevant_score = ast.literal_eval(row[3])
                    if callback:
                        callback_paras.append([_qid, doc_id, relevant_score])
                        #print callback_paras
                        callback(*callback_paras)
                        callback_paras.pop(-1)

    def get_all_judgments_callback(self, format, judgment, row_of_line):
        """
        @Input : 
            format - as of get_judgment_of_one_query
            judgment - for the return
            row_of_line - one line in judgment file after split into [qid, docid, relevant_score]
        """
        if row_of_line[0] not in judgment:
            if format == 'tuple' or format == 'list': 
                judgment[row_of_line[0]] = []
            elif format == 'dict':
                judgment[row_of_line[0]] = {}

        if format == 'tuple':
            judgment[row_of_line[0]].append((row_of_line[1], row_of_line[2]))
        elif format == 'list':
            judgment[row_of_line[0]].append([row_of_line[1], row_of_line[2]])
        elif format == 'dict':
            judgment[row_of_line[0]][row_of_line[1]] = row_of_line[2]

    def get_all_judgments(self, format='tuple'):
        """
        get the judgment of all queries of this corpus

        @Return: a dict {qid:[(docid, rel-score), ...], ...}
        """
        judgment = {}
        self.go_through_judgment_file(self.get_all_judgments_callback, [format, judgment])
        return judgment

    def get_judgment_of_some_queries(self, qids, format='tuple'):
        """
        get the judgment of a single query

        @Input: 
            qids (list) : a list contains the qid that to be returned
            format (string) : The return format: as "tuple" [(qid, rel-score),...], 
                "dict" {qid:rel-score,...}, "list" [[qid, rel-score],...]

        @Return: As parameter format indicates (See Above)
        """

        all_judgments = self.get_all_judgments(format)
        return {k: all_judgments.get(k, None) for k in qids}

    def get_relevant_docs_of_some_queries(self, qids, split=1, format='tuple'):
        """
        get the relevant documents of a single query

        @Input: 
            qid (list) : a list contains the qid that to be returned
            split (int) : how to define the "relevant documents". 
                Documents have equal or higher value than the split will be 
                viewed as relevant.
            format (string) : The return format: as "tuple" [(qid, rel-score),...], 
                "dict" {qid:rel-score,...}, "list" [[qid, rel-score],...]

        @Return: As parameter format indicates (See Above)
        """

        judgments = self.get_judgment_of_some_queries(qids, format)
        if format == 'dict':
            judgment = {k:{docid:judgments[k][docid] for docid in judgments[k] if judgments[k][docid] >= split} for k in judgments}
        elif format == 'tuple' or format == 'list':
            judgment = {k:[e for e in judgments[k] if e[1] >= split] for k in judgments}

        return judgment


if __name__ == '__main__':
    j = Judgment('../../wt2g')
    print j.get_judgment_of_some_queries(['401'])
    raw_input()
    print j.get_judgment_of_some_queries(['401', '402'], 'dict')
    raw_input()
    print j.get_relevant_docs_of_some_queries(['401'], 1, 'list')
    raw_input()
    print j.get_relevant_docs_of_some_queries(['401', '403'], 1, 'tuple')
    raw_input()
    print j.get_relevant_docs_of_some_queries(['401', '403'], 1, 'dict')


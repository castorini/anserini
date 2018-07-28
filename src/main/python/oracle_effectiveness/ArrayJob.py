import sys, os
from string import Template

qs_file_content = Template("""
#!/bin/sh  
#$$ -t 1-$nodes_range
#$$ -l m_mem_free=$memory,h_rt=4:00:00

source ~/after_login

SEEDFILE=$seedfile
SEED=$$(awk "NR==$$SGE_TASK_ID" $$SEEDFILE)

$s $quote$$SEED$quote
""")

qs_file_content_no_para = Template("""
#!/bin/sh  
#$$ -t 1-$nodes_range
#$$ -l m_mem_free=$memory

source ~/after_login

$s
""")


class ArrayJob():
    def __init__(self):
        pass

    def output_batch_qs_file(self, fn, command, quote_command=False, use_para_file=True, para_file=None, nodes_cnt=1, _memory='2G'):
        #print fn, command
        if not para_file:
            print 'Para File Empty!! Exit()'
            exit()

        quote = '"' if quote_command else ''

        with open(fn, 'wb') as f:
          if use_para_file:
            f.write(qs_file_content.substitute(s=command, seedfile=para_file, quote=quote, nodes_range=nodes_cnt, memory=_memory))
          else:
            f.write(qs_file_content_no_para.substitute(s=command, nodes_range=nodes_cnt, memory=_memory))
           


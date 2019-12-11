# -*- coding: utf-8 -*-
'''
Anserini: A Lucene toolkit for replicable information retrieval research

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
'''

from subprocess import call, Popen, PIPE

def run_shell_command(command, logger, echo=False, capture=False):
    process = Popen(command, shell=True, stdout=PIPE)

    if not echo:
        output, err = process.communicate()
        if process.returncode == 0: # success
            if capture == False: return process.returncode
            else: return output.decode('utf8').split('\n')
        else:
            raise RuntimeError('Error running shell comand: {}'.format(command))
    else:
        arr = []
        # Modified from https://fabianlee.org/2019/09/15/python-getting-live-output-from-subprocess-using-poll/
        while True:
            output = process.stdout.readline()
            if process.poll() is not None: break
            if output:
                logger.info('subprocess - ' + output.decode('utf8').strip())
                if capture: arr.append(output.decode('utf8').strip())
        # Make sure we read the output completely
        while True:
            output = process.stdout.readline()
            if not output: break
            logger.info('subprocess - ' + output.decode('utf8').strip())
            if capture: arr.append(output.decode('utf8').strip())
        if capture: return arr
        else: return process.returncode
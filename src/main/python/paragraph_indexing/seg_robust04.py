"""
Anserini: A toolkit for reproducible information retrieval research built on Lucene

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

import argparse
import json
import logging
import os

from .paraseg import FBISParaSegmenter, FR94ParaSegmenter, FTParaSegmenter, LAParaSegmenter
from .utils import TgzReader, safe_mkdir, form_json

SEGMENTER = {
    'FB': FBISParaSegmenter,
    'FR': FR94ParaSegmenter,
    'FT': FTParaSegmenter,
    'LA': LAParaSegmenter
}

if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG,
                        format='%(asctime)s %(message)s', datefmt='%m/%d/%Y %I:%M:%S ')

    parser = argparse.ArgumentParser()
    parser.add_argument("--input", '-i', type=str,
                        help='path to input tgz file', required=True)
    parser.add_argument("--output", '-o', type=str,
                        help='path to output folder', required=True)

    args = parser.parse_args()
    input_path = args.input
    output_path = args.output

    # preprocessing
    safe_mkdir(output_path)

    # start to segment
    counter = 0

    logging.info('start to segment files from %s', input_path)
    reader = TgzReader(input_path)
    while reader.hasnext():
        counter += 1
        if counter % 100000 == 0:
            logging.info('%d files have been processed', counter)

        docname, content_buffer = reader.next()
        if docname[:2] in SEGMENTER:
            segmenter = SEGMENTER[docname[:2]](content_buffer)
        else:
            raise TypeError('Invalid file type')

        paraid = 0
        parajsonarray = []
        while segmenter.hasnextpara():
            parastr = segmenter.nextpara()
            if len(parastr) < 50:
                continue

            paraid += 1
            if paraid >= 10000:
                logging.info('document %s has more than 10000 paragraphs...', docname)
                break
            parajsonarray.append(form_json(docname, paraid, parastr))

        # This is an empty file
        if paraid == 0:
            paraid += 1
            parastr = ''
            parajsonarray.append(form_json(docname, paraid, parastr))

        jsonstr = json.dumps(parajsonarray, separators=(',', ':'), indent=2)

        with open(os.path.join(output_path, '{}.json'.format(docname)), 'w') as f:
            f.write(jsonstr)

    logging.info('%d files have been segmented into paragraphs stored in %s', counter, output_path)
    reader.close()

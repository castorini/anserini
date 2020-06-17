'''
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
'''

import json
import xmltodict
import os

def convert_collection(args):
    print("Converting collection...")
    input_path = os.path.join(args.collection_path)
    output_path = os.path.join(args.output_folder)

    with open(output_path + "output.json", 'w') as out_file:
        out_file.write('[\n')
        for xml_file in os.listdir(input_path + 'datasets_xml'):
            print(xml_file)
            with open(os.path.join('datasets_xml', xml_file), 'r', encoding='utf8') as f:
                xml_string = f.read()

            json_string = json.dumps(xmltodict.parse(xml_string), indent=4)

            out_file.write(json_string)
            out_file.write(',')

        out_file.write('\n]')

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Converts iso19115 xml files to json files.')
    parser.add_argument('--collection', required=True, help='iso19115 collection file')
    parser.add_argument('--output', required=True, help='output folder')
    # not used yet since dataset is very small
    parser.add_argument('--max-doc-per-file', default=1000000, type=int, help='maximum number of documents in each jsonl file.')

    args = parser.parse_args()

    if not os.path.exists(args.output_folder):
        os.makedirs(args.output_folder)

    convert_collection(args)
    print('Done!')


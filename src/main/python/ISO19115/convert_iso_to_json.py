import json
import os
import argparse

def convert_collection(args):

    print("Converting collection...")
    # initiating input json
    input_path = os.path.join(args.collection_path)
    input_file = open(input_path)
    json_array = json.load(input_file)

    # initializing output jsonl file
    output_path = os.path.join(args.output_folder, "output.json")
    output_jsonl_file = open(output_path, 'w', encoding='utf-8', newline='\n')
    document_count = 0

    # iterating through the json objects
    for json_object in json_array:
        # extracting the article id and abstract
        id = json_object["gmd:MD_Metadata"]["gmd:fileIdentifier"]["gco:CharacterString"][:-8]
        title = json_object["gmd:MD_Metadata"]["gmd:identificationInfo"]["gmd:MD_DataIdentification"]["gmd:citation"]["gmd:CI_Citation"]["gmd:title"]["gco:CharacterString"]
        abstract = json_object["gmd:MD_Metadata"]["gmd:identificationInfo"]["gmd:MD_DataIdentification"]["gmd:abstract"]["gco:CharacterString"]
        # writing the new json to output.json
        new_json = {"id": id, "title":title,"contents": abstract}
        #new_json = {"id": id,"contents": abstract}
        output_jsonl_file.write(json.dumps(new_json) + '\n')
        document_count += 1

        if document_count % 1000 == 0:
            print(document_count, "documents have been converted")

    output_jsonl_file.close()
    print("Converted", document_count, "documents")


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='''Converts iso19115 files to Anserini jsonl files.''')
    parser.add_argument('--collection_path', required=True, help='iso19115 collection file')
    parser.add_argument('--output_folder', required=True, help='output file')
    # not used yet since dataset is very small
    parser.add_argument('--max_docs_per_file', default=1000000, type=int, help='maximum number of documents in each jsonl file.')

    args = parser.parse_args()

    if not os.path.exists(args.output_folder):
        os.makedirs(args.output_folder)

    convert_collection(args)
    print('Done!')

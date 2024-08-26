import json
import torch
import sys
from safetensors.torch import load_file

def convert_safetensors_to_dicts(vectors_path, docids_path):
    # Load vectors and docids
    vectors_tensor = load_file(vectors_path)['vectors']
    docids_tensor = load_file(docids_path)['docids']

    # Convert docids_tensor to a list of docid strings
    docids = ["".join([chr(int(c)) for c in row if c != 0]) for row in docids_tensor.tolist()]

    vectors_dict = {docids[i]: vectors_tensor[i].tolist() for i in range(len(docids))}
    contents_dict = {docid: {"docid": docid, "contents": "Dummy contents for docid: " + docid, "vector": vectors_dict[docid]} for docid in docids}

    return vectors_dict, contents_dict

def compare_dicts(vectors_dict, contents_dict):
    all_docids = set(vectors_dict.keys()).union(contents_dict.keys())

    differences = []

    for docid in sorted(all_docids):
        vector = vectors_dict.get(docid)
        content_entry = contents_dict.get(docid)
        
        if not vector or not content_entry:
            differences.append(f"Missing entry for docid: {docid}")
            continue

        content_docid = content_entry.get('docid')

        if docid != content_docid:
            differences.append(f"Docid mismatch for docid: {docid}, content docid: {content_docid}")
        else:
            if not vector == content_entry.get('vector'):
                differences.append(f"Vector mismatch for docid: {docid}")

    if differences:
        print("Differences found:")
        for difference in differences:
            print(difference)
    else:
        print("No differences found. The files are identical.")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python compare_safetensors.py <vectors.safetensors> <docids.safetensors>")
        sys.exit(1)

    vectors_path = sys.argv[1]
    docids_path = sys.argv[2]

    # Convert SafeTensors to dictionaries
    vectors_dict, contents_dict = convert_safetensors_to_dicts(vectors_path, docids_path)

    # Compare the dictionaries
    compare_dicts(vectors_dict, contents_dict)

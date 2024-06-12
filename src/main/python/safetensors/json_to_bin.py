import json
import torch
import os
import subprocess
from safetensors.torch import save_file

# Base directory relative to the expected script execution path in the Anserini repository
base_directory = './collections/beir-v1.0.0/bge-base-en-v1.5.safetensors/nfcorpus'

# Complete path to the JSONL file (assuming it's gzipped and needs to be decompressed)
jsonl_gz_file = os.path.join(base_directory, 'vectors.part00.jsonl.gz')

# Check if the gzipped file exists and unzip it
if os.path.exists(jsonl_gz_file):
    subprocess.run(['gzip', '-d', jsonl_gz_file], check=True)
    print(f"Unzipped the file in the directory {base_directory}")
else:
    print(f"File not found: {jsonl_gz_file}")
    exit(1)  # Exit if the file does not exist to avoid further errors

# Process all JSONL files in the input directory
for input_filename in os.listdir(base_directory):
    if input_filename.endswith('.jsonl'):
        input_file_path = os.path.join(base_directory, input_filename)
        
        # Extract the base name (e.g., "vectors.part00" from "vectors.part00.jsonl")
        base_name = os.path.splitext(input_filename)[0]
        
        # Define paths for output files using the new naming convention
        vectors_path = os.path.join(base_directory, f'{base_name}_vectors.safetensors')
        docids_path = os.path.join(base_directory, f'{base_name}_docids.safetensors')
        docid_to_idx_path = os.path.join(base_directory, f'{base_name}_docid_to_idx.json')

        # Initialize lists to hold data
        vectors = []
        docids = []

        # Process the JSONL file to extract vectors and docids
        with open(input_file_path, 'r') as file:
            for line in file:
                entry = json.loads(line)
                if isinstance(entry['vector'][0], float):
                    vectors.append(entry['vector'])
                    docids.append(entry['docid'])
                else:
                    print(f"Skipped invalid vector entry with docid: {entry['docid']}")

        # Convert lists to tensors
        vectors_tensor = torch.tensor(vectors, dtype=torch.float64)
        docid_to_idx = {docid: idx for idx, docid in enumerate(set(docids))}
        idxs = [docid_to_idx[docid] for docid in docids]
        docids_tensor = torch.tensor(idxs, dtype=torch.int64)

        # Save the tensors to SafeTensors files
        save_file({'vectors': vectors_tensor}, vectors_path)
        save_file({'docids': docids_tensor}, docids_path)

        # Save the docid_to_idx mapping to a JSON file
        with open(docid_to_idx_path, 'w') as f:
            json.dump(docid_to_idx, f)

        print(f"Saved vectors to {vectors_path}")
        print(f"Saved docids to {docids_path}")
        print(f"Saved docid_to_idx mapping to {docid_to_idx_path}")

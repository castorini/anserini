import json
import torch
import os
import argparse
import gzip
from safetensors.torch import save_file

# Set up argument parser
parser = argparse.ArgumentParser(description='Process vectors and docids from JSONL or GZ files.')
parser.add_argument('--input', required=True, help='Path to the input JSONL or GZ file')
parser.add_argument('--output', required=True, help='Path to the output directory')

args = parser.parse_args()

# Define paths
input_file_path = args.input
output_directory = args.output

# Ensure the output directory exists
if not os.path.exists(output_directory):
		os.makedirs(output_directory)

# Check if the input file is a .gz file and convert it to .jsonl if necessary
if input_file_path.endswith('.gz'):
		with gzip.open(input_file_path, 'rt') as gz_file:
				jsonl_file_path = input_file_path.replace('.gz', '.jsonl')
				with open(jsonl_file_path, 'w') as jsonl_file:
						for line in gz_file:
								jsonl_file.write(line)
		input_file_path = jsonl_file_path

# Check if the input file is a .jsonl file
elif not input_file_path.endswith('.jsonl'):
		raise ValueError("Input file must be a .jsonl or .gz file")

# Get the base name of the input file for output file names
base_name = os.path.basename(input_file_path).replace('.jsonl', '')

vectors_path = os.path.join(output_directory, f'{base_name}_vectors.safetensors')
docids_path = os.path.join(output_directory, f'{base_name}_docids.safetensors')

# Initialize lists to hold data
vectors = []
docids = []

# Process the JSONL file to extract vectors and docids
with open(input_file_path, 'r') as file:
		for line in file:
				entry = json.loads(line)
				# Ensure that the vector starts with a valid number
				if isinstance(entry['vector'][0], float):
						vectors.append(entry['vector'])
						docids.append(entry['docid'])
						# Uncomment the following line to print processed docid and vector info
						# print(f"Processed and saved docid: {entry['docid']} and the vectors start with: {entry['vector'][:1]}")
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
docid_to_idx_path = os.path.join(output_directory, f'{base_name}_docid_to_idx.json')
with open(docid_to_idx_path, 'w') as f:
		json.dump(docid_to_idx, f)

print(f"Saved vectors to {vectors_path}")
print(f"Saved docids to {docids_path}")
print(f"Saved docid_to_idx mapping to {docid_to_idx_path}")

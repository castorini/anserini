import json
import torch
import os
import gzip
from safetensors.torch import save_file, load_file

# Define paths
input_file_path = "/home/p2ojaghi/anserini/anserini/src/main/python/safetensors/sample_input.jsonl"
output_directory = "/home/p2ojaghi/anserini/anserini/src/main/python/safetensors"

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
            docid = entry['docid']
            docid_ascii = [ord(char) for char in docid]  # Convert docid to ASCII values
            docids.append(docid_ascii)
        else:
            print(f"Skipped invalid vector entry with docid: {entry['docid']}")

# Convert lists to tensors
vectors_tensor = torch.tensor(vectors, dtype=torch.float32)  # Use float32 for memory efficiency
docids_tensor = torch.nn.utils.rnn.pad_sequence([torch.tensor(d, dtype=torch.int64) for d in docids], batch_first=True)

# Debugging: Print out the first few document IDs and vectors
print("Sample document IDs (ASCII):", docids[:5])
print("Sample vectors:", vectors[:5])

# Save the tensors to SafeTensors files
save_file({'vectors': vectors_tensor}, vectors_path)
save_file({'docids': docids_tensor}, docids_path)

print(f"Saved vectors to {vectors_path}")
print(f"Saved docids to {docids_path}")

vectors_path = '/home/p2ojaghi/anserini/anserini/src/main/python/safetensors/sample_input_vectors.safetensors'
docids_path = '/home/p2ojaghi/anserini/anserini/src/main/python/safetensors/sample_input_docids.safetensors'

# Load vectors and docids
loaded_vectors = load_file(vectors_path)['vectors']
loaded_docids = load_file(docids_path)['docids']

print(f"Loaded vectors: {loaded_vectors}")
print(f"Loaded document IDs (ASCII): {loaded_docids}")
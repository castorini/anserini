import json
from safetensors.torch import save_file
import torch
import os

# Define paths
input_file_path = '../../../../collections/vectors.part00.jsonl'
output_directory = '../../../../target/safetensors'
if not os.path.exists(output_directory):
    os.makedirs(output_directory)

vectors_path = os.path.join(output_directory, 'vectors.safetensors')
docids_path = os.path.join(output_directory, 'docids.safetensors')

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
          #  print(f"Processed and saved docid: {entry['docid']} and the vectors start with: {entry['vector'][:1]}")
        else:
            print(f"Skipped invalid vector entry with docid: {entry['docid']}")

# print("type(vectors): ", type(vectors))
# print("type(vector[0]): ", type(vectors[0][0]))
# print("type(docids): ", type(docids))
# print("type(docids[0]): ", type(docids[0][0]))
# print("len(vectors): ", len(vectors))
# print("len(docids): ", len(docids))
# Convert lists to tensors
vectors_tensor = torch.tensor(vectors, dtype=torch.float64)
docid_to_idx = {docid: idx for idx, docid in enumerate(set(docids))}
idxs = [docid_to_idx[docid] for docid in docids]
docids_tensor = torch.tensor(idxs, dtype=torch.int64)

# print("type(vectors_tensor): ", type(vectors_tensor))
# print("type(docids_tensor): ", type(docids_tensor))
# print("vectors_tensor.shape: ", vectors_tensor.shape)
# print("docids_tensor.shape: ", docids_tensor.shape)
# print("docids_tensor[:10]: ", type(docids_tensor[:10]))



# Save the tensors to SafeTensors files
save_file({'vectors': vectors_tensor}, vectors_path)
save_file({'docids': docids_tensor}, docids_path)

# Save the docid_to_idx mapping to a JSON file
docid_to_idx_path = os.path.join(output_directory, 'docid_to_idx.json')
with open(docid_to_idx_path, 'w') as f:
    json.dump(docid_to_idx, f)

print(f"Saved vectors to {vectors_path}")
print(f"Saved docids to {docids_path}")

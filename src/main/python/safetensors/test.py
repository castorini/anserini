import numpy as np
import torch
import time
import tracemalloc
import json
import matplotlib.pyplot as plt
from safetensors.torch import save_file, load_file

# Define paths
file_path_vectors = 'output/vectors.safetensors'
file_path_docids = 'output/docids.safetensors'

def measure_performance(file_path):
    """
    Measures the time and memory used for loading data with SafeTensors.
    """
    # Measure load performance
    start_time_load = time.time()
    tracemalloc.start()
    loaded_data = load_file(file_path)
    current_load, peak_load = tracemalloc.get_traced_memory()
    time_taken_load = time.time() - start_time_load
    tracemalloc.stop()

    print(f"Time taken to load: {time_taken_load} seconds")
    print(f"Memory used: {current_load / 10**6} MB; Peak: {peak_load / 10**6} MB")

    return time_taken_load, current_load, peak_load
    
def verify_data_integrity(original_data, loaded_data, key):
    """
    Verifies that data remains unchanged after saving and loading with SafeTensors.

    Parameters:
    - original_data: The original data before saving.
    - loaded_data: The data loaded from the SafeTensors file.
    - key: The key used to save and thus to load the data.
    """
    original_tensor = torch.tensor(original_data, dtype=loaded_data[key].dtype)
    if not torch.equal(original_tensor, loaded_data[key]):
        print(f"Data integrity check failed for {key}!")
        print(f"Original data type: {original_tensor.dtype}, Loaded data type: {loaded_data[key].dtype}")
        print(f"Original shape: {original_tensor.shape}, Loaded shape: {loaded_data[key].shape}")
        if original_tensor.shape == loaded_data[key].shape:
            diff = original_tensor != loaded_data[key]
            print(f"Number of mismatched entries: {diff.sum()}")
            print(f"Mismatched original data: {original_tensor[diff]}")
            print(f"Mismatched loaded data: {loaded_data[key][diff]}")
        assert False, "Mismatch detected."
    else:
        print(f"Data integrity verified for {key}.")


def check_data_type(data, key):
    """
    Checks the data type of the loaded data.

    Parameters:
    - data: The loaded data.
    - key: The key used to save and thus to load the data.
    """
    if not isinstance(data[key], torch.Tensor):
        print(f"Data type check failed for {key}!")
        print(f"Expected data type: torch.Tensor, Actual data type: {type(data[key])}")
        assert False, "Data type mismatch detected."
    else:
        print(f"Data type verified for {key}.")
# Load the original data for integrity check
# Replace 'load_original_data' with the actual function or method you have to load the original data.
# If the original data is not available in the memory, you need to load it from where it was saved before conversion to SafeTensors.
# Initialize lists to hold data
input_file_path = './input/vectors.part00.jsonl'
vectors = []
docids = []

# Process the JSONL file to extract vectors and docids
with open(input_file_path, 'r') as file:
    for line in file:
        entry = json.loads(line)
        vectors.append(entry['vector'])
        docids.append(entry['docid'])
        
# For vectors
original_vectors = vectors  # This function should be defined by you
print(len(original_vectors))
print(len(original_vectors[2]))
time_taken_load_vectors, current_load_vectors, peak_load_vectors = measure_performance(file_path_vectors)
loaded_vectors = load_file(file_path_vectors)
# For vectors
verify_data_integrity(original_vectors, loaded_vectors, 'vectors')

# For docids
# Since docids are not numbers, we should convert them to their corresponding index values.
original_docids = docids  # This function should be defined by you
loaded_docids = load_file(file_path_docids)
# Load the docid_to_idx mapping from the JSON file
docid_to_idx_path = 'output/docid_to_idx.json'  # Update with your actual path
with open(docid_to_idx_path, 'r') as f:
    docid_to_idx = json.load(f)
# Now you can use this loaded mapping to convert original_docids to indices
# and verify the data integrity using verify_data_integrity function
original_docids_idx = [docid_to_idx[docid] for docid in original_docids]
verify_data_integrity(original_docids_idx, loaded_docids, 'docids')
check_data_type(loaded_vectors, 'vectors')
check_data_type(loaded_docids, 'docids')


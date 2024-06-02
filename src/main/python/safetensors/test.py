import os
import json
import torch
import time
import tracemalloc
from safetensors.torch import load_file
import matplotlib.pyplot as plt

# Define directories
output_directory = '../../../../target/safetensors'
vectors_directory = os.path.join(output_directory, 'vectors')
docids_directory = os.path.join(output_directory, 'docids')
docid_to_idx_directory = os.path.join(output_directory, 'docid_to_idx')
input_directory = '../../../../collections/beir-v1.0.0/bge-base-en-v1.5/nfcorpus'

# Function to measure performance
def measure_performance(file_path):
    start_time_load = time.time()
    tracemalloc.start()
    loaded_data = load_file(file_path)
    current_load, peak_load = tracemalloc.get_traced_memory()
    time_taken_load = time.time() - start_time_load
    tracemalloc.stop()

    print(f"Time taken to load: {time_taken_load} seconds")
    print(f"Memory used: {current_load / 10**6} MB; Peak: {peak_load / 10**6} MB")

    return time_taken_load, current_load, peak_load

# Function to verify data integrity
def verify_data_integrity(original_data, loaded_data, key):
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

# Function to check data type
def check_data_type(data, key):
    if not isinstance(data[key], torch.Tensor):
        print(f"Data type check failed for {key}!")
        print(f"Expected data type: torch.Tensor, Actual data type: {type(data[key])}")
        assert False, "Data type mismatch detected."
    else:
        print(f"Data type verified for {key}.")

# Verify the performance and integrity of the processed files
for base_name in os.listdir(input_directory):
    if base_name.endswith('.json'):
        base_name = os.path.splitext(base_name)[0]

        file_path_vectors = os.path.join(vectors_directory, f'{base_name}_vectors.safetensors')
        file_path_docids = os.path.join(docids_directory, f'{base_name}_docids.safetensors')
        docid_to_idx_path = os.path.join(docid_to_idx_directory, f'{base_name}_docid_to_idx.json')

        # Load the original data for integrity check
        vectors = []
        docids = []

        # Process the JSONL file to extract vectors and docids
        input_file_path = os.path.join(input_directory, f'{base_name}.jsonl')
        with open(input_file_path, 'r') as file:
            for line in file:
                entry = json.loads(line)
                vectors.append(entry['vector'])
                docids.append(entry['docid'])

        # Measure performance for vectors
        time_taken_load_vectors, current_load_vectors, peak_load_vectors = measure_performance(file_path_vectors)
        loaded_vectors = load_file(file_path_vectors)
        verify_data_integrity(vectors, loaded_vectors, 'vectors')
        check_data_type(loaded_vectors, 'vectors')

        # Measure performance for docids
        time_taken_load_docids, current_load_docids, peak_load_docids = measure_performance(file_path_docids)
        loaded_docids = load_file(file_path_docids)

        # Load the docid_to_idx mapping from the JSON file
        with open(docid_to_idx_path, 'r') as f:
            docid_to_idx = json.load(f)

        # Convert original docids to indices and verify data integrity
        original_docids_idx = [docid_to_idx[docid] for docid in docids]
        verify_data_integrity(original_docids_idx, loaded_docids, 'docids')
        check_data_type(loaded_docids, 'docids')

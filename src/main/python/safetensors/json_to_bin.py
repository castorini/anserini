import os
import json
import gzip
import torch
import argparse
import shutil
import logging
from safetensors.torch import save_file, load_file
from concurrent.futures import ThreadPoolExecutor, as_completed
from tqdm import tqdm


def setup_logging():
    logging.basicConfig(
        format="%(asctime)s - %(levelname)s - %(message)s",
        level=logging.ERROR,
        handlers=[
            logging.StreamHandler()  # Logs to the terminal
        ]
    )


def read_jsonl_file(file_path: str) -> list[dict]:
    data = []
    try:
        if file_path.endswith(".gz"):
            with gzip.open(file_path, "rt", encoding="utf-8") as f:
                for line in f:
                    data.append(json.loads(line))
        else:
            with open(file_path, "r", encoding="utf-8") as f:
                for line in f:
                    data.append(json.loads(line))
    except Exception as e:
        logging.error(f"Failed to read file {file_path}: {e}")
        raise RuntimeError(f"Failed to read file {file_path}: {e}")
    return data


def convert_file_to_safetensors(input_file_path: str, vectors_path: str, docids_path: str) -> int:
    try:
        data = read_jsonl_file(input_file_path)
        vectors = []
        docids = []
        
        for entry in data:
            if isinstance(entry.get('vector', [None])[0], float):
                vectors.append(entry['vector'])
                docid = entry['docid']
                docid_ascii = [ord(char) for char in docid]  # Convert docid to ASCII values
                docids.append(docid_ascii)
            else:
                logging.warning(f"Skipped invalid vector entry with docid: {entry.get('docid', 'N/A')}")

        # Convert to tensors
        vectors_tensor = torch.tensor(vectors, dtype=torch.float64)
        docids_tensor = torch.nn.utils.rnn.pad_sequence([torch.tensor(d, dtype=torch.int64) for d in docids], batch_first=True)
        
        # Save as Safetensors
        save_file({'vectors': vectors_tensor}, vectors_path)
        save_file({'docids': docids_tensor}, docids_path)

        return len(vectors)  # Return number of processed entries

    except Exception as e:
        logging.error(f"Error converting {input_file_path} to Safetensors: {e}")
        raise RuntimeError(f"Error converting {input_file_path} to Safetensors: {e}")


def validate_safetensor_conversion(vectors_path: str, docids_path: str, original_data: list[dict]) -> bool:
    try:
        loaded_vectors = load_file(vectors_path)['vectors']
        loaded_docids = load_file(docids_path)['docids']
        
        # Validate the sizes
        if loaded_vectors.size(0) != len(original_data):
            raise ValueError(f"Validation failed for {vectors_path}: number of vectors does not match the original data")
        
        logging.info(f"Validation successful for {vectors_path} and {docids_path}")
        return True

    except Exception as e:
        logging.error(f"Validation failed for {vectors_path} or {docids_path}: {e}")
        raise e


def convert_and_validate_file(input_file_path: str, vectors_path: str, docids_path: str) -> int:
    row_count = convert_file_to_safetensors(input_file_path, vectors_path, docids_path)
    original_data = read_jsonl_file(input_file_path)
    validate_safetensor_conversion(vectors_path, docids_path, original_data)
    logging.info(f"Converted {input_file_path} to {vectors_path} and {docids_path}")
    return row_count


def convert_jsonl_to_safetensors(input_dir: str, output_dir: str, overwrite=False) -> None:
    if overwrite and os.path.exists(output_dir):
        shutil.rmtree(output_dir)

    os.makedirs(output_dir, exist_ok=True)

    seen_basenames = set()
    total_files = 0
    total_rows = 0

    files_to_process = []
    for file_name in os.listdir(input_dir):
        input_file_path = os.path.join(input_dir, file_name)

        if file_name.endswith(".jsonl"):
            basename = file_name[:-6]
        elif file_name.endswith(".jsonl.gz"):
            basename = file_name[:-9]
        else:
            continue

        if basename in seen_basenames:
            continue

        seen_basenames.add(basename)
        vectors_path = os.path.join(output_dir, f"{basename}_vectors.safetensors")
        docids_path = os.path.join(output_dir, f"{basename}_docids.safetensors")
        files_to_process.append((input_file_path, vectors_path, docids_path))

    with tqdm(total=len(files_to_process), desc="Processing Files") as pbar:
        for input_path, vectors_path, docids_path in files_to_process:
            try:
                logging.info(f"Processing file: {input_path}")
                row_count = convert_and_validate_file(input_path, vectors_path, docids_path)
                total_files += 1
                total_rows += row_count
            except Exception as e:
                logging.error(f"Failed to process {input_path}: {e}")
            finally:
                pbar.update(1)

    logging.info(f"Total files processed: {total_files}")
    logging.info(f"Total rows processed: {total_rows}")

if __name__ == "__main__":
    setup_logging()

    parser = argparse.ArgumentParser(
        description="Convert JSONL files to Safetensor format and validate."
    )
    parser.add_argument(
        "--input", required=True, help="Input directory containing JSONL files."
    )
    parser.add_argument(
        "--output", required=True, help="Output directory for Safetensor files."
    )
    parser.add_argument(
        "--overwrite",
        action="store_true",
        default=False,
        help="Overwrite the output directory.",
    )
    args = parser.parse_args()

    convert_jsonl_to_safetensors(args.input, args.output, args.overwrite)

import os
import json
import gzip
import pandas as pd
import argparse
import shutil
import logging
from concurrent.futures import ThreadPoolExecutor, as_completed
from tqdm import tqdm


def setup_logging():
    logging.basicConfig(
        format="%(asctime)s - %(levelname)s - %(message)s", level=logging.INFO
    )


def read_jsonl_file(file_path: str) -> list[dict]:
    """
    Reads a .jsonl or .jsonl.gz file and returns a list of dictionaries.
    """
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


def convert_file_to_parquet(input_file_path: str, output_file_path: str) -> int:
    """
    Converts a single JSONL file to Parquet format.
    """
    try:
        data = read_jsonl_file(input_file_path)
        df = pd.DataFrame(data)
        # contents is a placeholder field, can be dropped
        df.drop(columns=["contents"], inplace=True)

        # Write to Parquet
        df.to_parquet(output_file_path, index=False)
        return len(df)
    except Exception as e:
        logging.error(f"Error converting {input_file_path} to Parquet: {e}")
        raise RuntimeError(f"Error converting {input_file_path} to Parquet: {e}")


def validate_parquet_conversion(input_file_path: str, parquet_file_path: str) -> bool:
    """
    Validates that the data in the Parquet file matches the data in the original JSONL file.
    """
    try:
        # Read original JSONL data
        jsonl_data = read_jsonl_file(input_file_path)
        jsonl_df = pd.DataFrame(jsonl_data)
        jsonl_df.drop(columns=["contents"], inplace=True)

        # Read Parquet data
        parquet_df = pd.read_parquet(parquet_file_path)

        # Check schema consistency
        if list(jsonl_df.columns) != list(parquet_df.columns):
            error_message = f"Schema mismatch for {input_file_path}: JSONL columns {jsonl_df.columns} vs Parquet columns {parquet_df.columns}"
            logging.error(error_message)
            raise ValueError(error_message)

        # Check row count
        if len(jsonl_df) != len(parquet_df):
            error_message = f"Row count mismatch for {input_file_path}: JSONL has {len(jsonl_df)} rows, Parquet has {len(parquet_df)} rows"
            logging.error(error_message)
            raise ValueError(error_message)

        return True

    except Exception as e:
        raise e


def convert_and_validate_file(input_file_path: str, output_file_path: str) -> int:
    """
    Converts a single JSONL file to Parquet format and then validates it.
    """
    row_count = convert_file_to_parquet(input_file_path, output_file_path)
    validate_parquet_conversion(input_file_path, output_file_path)
    logging.info(f"Converted {input_file_path} to {output_file_path}")
    return row_count


def convert_jsonl_to_parquet(input_dir: str, output_dir: str, overwrite=False) -> None:
    """
    Converts all JSONL files in the input directory to Parquet format in the output directory.
    """
    if overwrite and os.path.exists(output_dir):
        # Remove the existing output directory if overwrite is True
        shutil.rmtree(output_dir)

    # Create the output directory if it does not exist
    os.makedirs(output_dir, exist_ok=True)

    # Track seen basenames to avoid processing duplicates
    seen_basenames = set()
    total_files = 0
    total_rows = 0

    # List all files to be processed
    files_to_process = []
    for file_name in os.listdir(input_dir):
        input_file_path = os.path.join(input_dir, file_name)

        # Handle cases for .jsonl and .jsonl.gz files
        if file_name.endswith(".jsonl"):
            basename = file_name[:-6]  # Remove .jsonl extension
        elif file_name.endswith(".jsonl.gz"):
            basename = file_name[:-9]  # Remove .jsonl.gz extension
        else:
            continue

        # Skip if this basename has already been processed
        if basename in seen_basenames:
            continue

        seen_basenames.add(basename)
        output_file_path = os.path.join(output_dir, f"{basename}.parquet")
        files_to_process.append((input_file_path, output_file_path))

    # Process files concurrently
    with ThreadPoolExecutor() as executor:
        future_to_file = {
            executor.submit(convert_and_validate_file, input_path, output_path): (
                input_path,
                output_path,
            )
            for input_path, output_path in files_to_process
        }

        with tqdm(total=len(files_to_process), desc="Processing Files") as pbar:
            for future in as_completed(future_to_file):
                input_path, _ = future_to_file[future]
                try:
                    row_count = future.result()
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
        description="Convert JSONL files to Parquet format and validate."
    )
    parser.add_argument(
        "--input", required=True, help="Input directory containing JSONL files."
    )
    parser.add_argument(
        "--output", required=True, help="Output directory for Parquet files."
    )
    parser.add_argument(
        "--overwrite",
        action="store_true",
        default=False,
        help="Overwrite the output directory.",
    )
    args = parser.parse_args()

    convert_jsonl_to_parquet(args.input, args.output, args.overwrite)

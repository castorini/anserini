import os
import argparse
import logging
import faiss
import numpy as np
import pandas as pd
import shutil


def setup_logging():
    logging.basicConfig(
        format="%(asctime)s - %(levelname)s - %(message)s", level=logging.INFO
    )


def read_docid_file(docid_path):
    """
    Reads the docid file and returns a list of document IDs.
    """
    try:
        with open(docid_path, 'r') as f:
            docids = [line.strip() for line in f]
        logging.info(f"Read {len(docids)} docids from {docid_path}")
        return docids
    except Exception as e:
        logging.error(f"Failed to read docid file {docid_path}: {e}")
        raise RuntimeError(f"Failed to read docid file {docid_path}: {e}")


def read_faiss_index(index_path):
    """
    Reads a FAISS index file and returns a numpy array of vectors.
    """
    try:
        index = faiss.read_index(index_path)
        vectors = index.reconstruct_n(0, index.ntotal)
        logging.info(f"Read {vectors.shape[0]} vectors from {index_path}")
        return vectors
    except Exception as e:
        logging.error(f"Failed to read FAISS index file {index_path}: {e}")
        raise RuntimeError(f"Failed to read FAISS index file {index_path}: {e}")


def write_to_parquet_in_chunks(df, output_dir, rows_per_chunk=10**6):
    """
    Writes the DataFrame to Parquet files in chunks of specified size.
    """
    # Write DataFrame to Parquet in chunks
    for i in range(0, len(df), rows_per_chunk):
        chunk = df.iloc[i:i + rows_per_chunk]
        chunk_file = os.path.join(output_dir, f'chunk_{i//rows_per_chunk}.parquet')
        try:
            chunk.to_parquet(chunk_file, index=False)
            logging.info(f"Successfully wrote chunk to {chunk_file}")
        except Exception as e:
            logging.error(f"Failed to write chunk to Parquet file {chunk_file}: {e}")
            raise RuntimeError(f"Failed to write chunk to Parquet file {chunk_file}: {e}")


def convert_faiss_to_parquet(input_dir, output_dir, overwrite):
    """
    Converts FAISS index files in the input directory to Parquet files in the output directory.
    """
    # Ensure the input directory contains the necessary files
    docid_path = os.path.join(input_dir, 'docid')
    index_path = os.path.join(input_dir, 'index')

    if not os.path.isfile(docid_path) or not os.path.isfile(index_path):
        raise FileNotFoundError("Both 'docid' and 'index' files must be present in the input directory.")

    # Set up the output directory
    if os.path.exists(output_dir):
        if overwrite:
            shutil.rmtree(output_dir)
            os.makedirs(output_dir)
        else:
            raise FileExistsError(f"Output directory '{output_dir}' already exists. Use --overwrite to replace it.")
    else:
        os.makedirs(output_dir)

    # Read docids and vectors
    docids = read_docid_file(docid_path)
    vectors = read_faiss_index(index_path)

    # Check if the number of docids matches the number of vectors
    if len(docids) != vectors.shape[0]:
        error_message = "The number of docids does not match the number of vectors."
        logging.error(error_message)
        raise ValueError(error_message)

    df = pd.DataFrame({
        'docid': docids,
        'vector': vectors.tolist()  # Convert vectors to a list format
    })

    # Write DataFrame to Parquet in chunks
    write_to_parquet_in_chunks(df, output_dir)

if __name__ == "__main__":
    setup_logging()

    # Argument parsing
    parser = argparse.ArgumentParser(
        description="Convert FAISS index files to Parquet format in chunks."
    )
    parser.add_argument(
        "--input", required=True, help="Input directory containing 'docid' and 'index' files."
    )
    parser.add_argument(
        "--output", required=True, help="Output directory where the Parquet files will be saved."
    )
    parser.add_argument(
        "--overwrite",
        action="store_true",
        default=False,
        help="Overwrite the output directory if it already exists.",
    )
    args = parser.parse_args()

    try:
        # Convert FAISS index data to Parquet in chunks
        convert_faiss_to_parquet(args.input, args.output, args.overwrite)
    except Exception as e:
        logging.error(f"Script failed: {e}")


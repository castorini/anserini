#!/bin/bash

# Default base directory
DEFAULT_BASE_DIR="collections/faiss/"
BASE_DIR=${1:-$DEFAULT_BASE_DIR}

# Shift the arguments if a base directory is passed
if [ "$BASE_DIR" != "$DEFAULT_BASE_DIR" ]; then
  shift
fi

# Check if the --all flag is passed
if [ "$1" == "--all" ]; then
  # Get all subdirectories in the base directory
  SUBDIRS=("$BASE_DIR"/*/)
else
  # If no --all flag, use the provided arguments as subdirectory names
  if [ $# -eq 0 ]; then
    echo "No subdirectories specified. Exiting."
    exit 1
  fi

  # Convert the passed arguments to subdirectory paths
  SUBDIRS=()
  for SUBDIR_NAME in "$@"; do
    SUBDIR_PATH="$BASE_DIR/$SUBDIR_NAME"
    if [ -d "$SUBDIR_PATH" ]; then
      SUBDIRS+=("$SUBDIR_PATH/")
    else
      echo "Subdirectory $SUBDIR_PATH does not exist. Skipping."
    fi
  done

  # If no valid subdirectories were provided, exit
  if [ ${#SUBDIRS[@]} -eq 0 ]; then
    echo "No valid subdirectories provided. Exiting."
    exit 1
  fi
fi

# Loop through each specified subdirectory (or all subdirectories if --all was passed)
for SUBDIR in "${SUBDIRS[@]}"; do
  if [ -d "$SUBDIR" ]; then
    (
      echo "Processing $SUBDIR"

      PARQUET_DIR="${SUBDIR%/}.faiss-parquet"
      SUBDIR_NAME=$(basename "$SUBDIR")
      INDEX_NAME="indexes/faiss-parquet/$SUBDIR_NAME"
      RUNS_FILE="runs/${SUBDIR_NAME}_faiss_parquet.txt"
      EVAL_FILE="runs/${SUBDIR_NAME}_faiss_parquet_evals.txt"
      
      # Convert to Parquet
      python src/main/python/parquet/faiss_to_parquet.py --input "$SUBDIR" --output "$PARQUET_DIR" --overwrite

      # Index Parquet data
      bin/run.sh io.anserini.index.IndexFlatDenseVectors \
        -threads 16 \
        -collection ParquetDenseVectorCollection \
        -input "$PARQUET_DIR" \
        -generator DenseVectorDocumentGenerator \
        -index "$INDEX_NAME" \
       	>&"logs/debug-log.beir-v1.0.0-${SUBDIR_NAME}.bge-base-en-v1.5"

      # Search on the indexed data
      bin/run.sh io.anserini.search.SearchFlatDenseVectors \
        -index "$INDEX_NAME" \
        -topics "tools/topics-and-qrels/topics.beir-v1.0.0-${SUBDIR_NAME}.test.bge-base-en-v1.5.jsonl.gz" \
        -topicReader JsonStringVector \
        -output "$RUNS_FILE" \
        -hits 1000 -removeQuery -threads 16 
      echo "Running evaluations for $SUBDIR_NAME"
      {
        bin/trec_eval -c -m ndcg_cut.10 "tools/topics-and-qrels/qrels.beir-v1.0.0-${SUBDIR_NAME}.test.txt" "$RUNS_FILE"
        bin/trec_eval -c -m recall.100 "tools/topics-and-qrels/qrels.beir-v1.0.0-${SUBDIR_NAME}.test.txt" "$RUNS_FILE"
        bin/trec_eval -c -m recall.1000 "tools/topics-and-qrels/qrels.beir-v1.0.0-${SUBDIR_NAME}.test.txt" "$RUNS_FILE"
      } >"$EVAL_FILE"

      # Check if the commands were successful
      if [ $? -eq 0 ]; then
        echo "Successfully processed $SUBDIR"
      else
        echo "Failed to process $SUBDIR"
      fi
    ) & 
  fi
done

wait

echo "All specified subdirectories processed."

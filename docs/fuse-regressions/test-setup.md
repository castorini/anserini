# Fusion Regression Test Setup

This document provides instructions for setting up and downloading the necessary run files to perform fusion regression tests.

## Prerequisites
You will need the following:
- A working installation of `wget`.
- Enough disk space to store the downloaded files.

## Automatic Download Using Script
To automatically download the required files, you can use the following shell script. The script will download and extract the files in the `runs/runs.beir` folder with the correct filenames.

```bash
#!/bin/bash

# Create the target directory if it doesn't exist
mkdir -p runs/runs.beir

# Download the run files from Google Drive using their file IDs
wget --no-check-certificate 'https://drive.google.com/uc?export=download&id=1XVlVCDYQe3YjRzxplaeGbmW_0EFQCgm8' -O runs/runs.beir/run.inverted.beir-v1.0.0-robust04.multifield.test.bm25
wget --no-check-certificate 'https://drive.google.com/uc?export=download&id=1Z4rWlNgmXebMf1ardfiDg_4KIZImjqxt' -O runs/runs.beir/run.inverted.beir-v1.0.0-robust04.splade-pp-ed.test.splade-pp-ed-cached
wget --no-check-certificate 'https://drive.google.com/uc?export=download&id=1fExxJHkPPNCdtptKqWTbcsH0Ql0PnPqS' -O runs/runs.beir/run.inverted.beir-v1.0.0-robust04.flat.test.bm25

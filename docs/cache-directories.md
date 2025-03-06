# Cache Directories in Anserini

Anserini uses cache directories for storing various resources, such as:

- Pre-built indexes
- Encoder models
- Topics and qrels files

By default, these are stored in your home directory under `~/.cache/pyserini/`.

## Default Cache Paths

- Indexes: `~/.cache/pyserini/indexes/`
- Encoders: `~/.cache/pyserini/encoders/`
- Topics and Qrels: `~/.cache/pyserini/topics-and-qrels/`

## Customizing Cache Directories

You can customize these paths using environment variables.

### Using Environment Variables

```sh
# Set custom cache directory for indexes
export ANSERINI_INDEX_CACHE=/path/to/custom/index/cache

# Set custom cache directory for encoders
export ANSERINI_ENCODER_CACHE=/path/to/custom/encoder/cache

# Set custom cache directory for topics and qrels
export ANSERINI_TOPICS_CACHE=/path/to/custom/topics/cache
```

## Fallback Order

When resolving cache directories, Anserini checks for locations in the following order:

1. System property (e.g., `anserini.index.cache`)
2. Environment variable (e.g., `ANSERINI_INDEX_CACHE`)
3. Default location in the user home directory
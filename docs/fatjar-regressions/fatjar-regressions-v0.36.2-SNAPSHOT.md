# Anserini Fatjar Regresions (v0.36.2-SNAPSHOT)

❗ **This is a stub.**

Fetch the fatjar:

```bash
# Update once artifact has been published
wget https://repo1.maven.org/maven2/io/anserini/anserini/0.36.0/anserini-0.36.0-fatjar.jar
```

Note that prebuilt indexes will be downloaded to `~/.cache/pyserini/indexes/`.
Currently, this path is hard-coded (see [Anserini #2322](https://github.com/castorini/anserini/issues/2322)).
If you want to change the download location, the current workaround is to use symlinks, i.e., symlink `~/.cache/pyserini/indexes/` to the actual path you desire.

Let's start out by setting the `ANSERINI_JAR` and the `OUTPUT_DIR`:

```bash
export ANSERINI_JAR=`ls target/*-fatjar.jar`
export OUTPUT_DIR="runs"
```

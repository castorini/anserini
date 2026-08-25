# Anserini Fatjar Reproduction Notes (v2.3.0)

The Anserini fatjar v2.3.0 release occurred at the following commit:

```text
TBD
```

Agent skills in `.agents/skills/` capture exactly how to use Anserini.
Repo `HEAD` may have diverged from this specific release, so if you need _exactly_ this release, the best way to ensure consistent behavior is to rewind the repo back to the above commit.

## Cache

Anserini ships with "batteries included": it will automatically download prebuilt indexes, topics, and qrels on demand.
The base cache path is `~/.cache/pyserini` by default.
The `pyserini.cache` system property and `PYSERINI_CACHE` environment variable override the base cache path.
If neither override is set and a `.cache` directory exists in the current working directory, `.cache/pyserini` in the current directory is used as the base cache path.

## Reproductions from Prebuilt Indexes

Using Anserini, you can easily reproduce many retrieval runs on standard IR benchmark datasets.
The entire suite can be run using the following driver program:

```bash
nohup java -cp `ls *-fatjar.jar` io.anserini.reproduce.RunJavaReproductionCommands --config from-prebuilt-indexes --load 30 --max 3 --sleep 60 --logs-directory . --runs-directory . >& from-prebuilt-indexes.log &
```

Add the option `--dry-run` to get an enumeration of the individual IR benchmark datasets that are included by the driver as part of the suite.

When all the runs finish, use the following command to generate a summary:

```bash
java -cp `ls *-fatjar.jar` io.anserini.reproduce.SummarizeLogsFromPrebuiltIndexes --md --logs-directory .
```

Alternatively, you can perform runs on individual IR benchmark datasets (the driver program simply runs the following):

```bash
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --runs-directory . --config beir
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --runs-directory . --config bright
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --runs-directory . --config msmarco-v1-passage
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --runs-directory . --config msmarco-v1-doc
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --runs-directory . --config msmarco-v2-passage
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --runs-directory . --config msmarco-v2-doc
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --runs-directory . --config msmarco-v2.1-doc-segmented
java -cp `ls *-fatjar.jar` io.anserini.reproduce.ReproduceFromPrebuiltIndexes --runs-directory . --config msmarco-v2.1-doc
```

Again, use the `--dry-run` option to get an enumeration of individual commands without running them.

❗ When running `RunJavaReproductionCommands` you might get the following error:

```text
{"message":"API rate limit exceeded for 129.97.167.161. (But here's the good news: Authenticated requests get a higher rate limit. Check out the documentation for more details.)","documentation_url":"https://docs.github.com/rest/overview/resources-in-the-rest-api#rate-limiting"}
```

This is likely from `ReproduceFromPrebuiltIndexes`, which uses the GitHub REST API to query prebuilt index metadata.
To fix, either authenticate using [`gh`](https://github.com/cli/cli) or run above `ReproduceFromPrebuiltIndexes` sequence of commands one at a time to stay below the limit.

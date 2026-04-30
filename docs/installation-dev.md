# Anserini Dev Environment Installation for Users

You'll need Java 21 and Maven 3.9+ to build Anserini.
Clone our repo with the `--recurse-submodules` option to make sure the `eval/` submodule also gets cloned (alternatively, use `git submodule update --init`).
Then, build using Maven:

```bash
mvn clean package
```

The `tools/` directory, which contains evaluation tools and other scripts, is actually [this repo](https://github.com/castorini/anserini-tools), integrated as a [Git submodule](https://git-scm.com/book/en/v2/Git-Tools-Submodules) (so that it can be shared across related projects).
Build as follows (you might get warnings, but okay to ignore):

```bash
cd tools/eval && tar xvfz trec_eval.9.0.4.tar.gz && cd trec_eval.9.0.4 && make && cd ../../..
cd tools/eval/ndeval && make && cd ../../..
```

With that, you should be ready to go.

<details>
<summary>Windows tips</summary>

If you are using Windows, please use WSL2 to build Anserini.
Please refer to the [WSL2 Installation](https://learn.microsoft.com/en-us/windows/wsl/install) document to install WSL2 if you haven't already.

Note that on Windows without WSL2, tests may fail due to encoding issues, see [#1466](https://github.com/castorini/anserini/issues/1466).
A simple workaround is to skip tests by adding `-Dmaven.test.skip=true` to the above `mvn` command.
See [#1121](https://github.com/castorini/pyserini/discussions/1121) for additional discussions on debugging Windows build errors.

</details>

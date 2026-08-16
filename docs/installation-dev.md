# Anserini Dev Environment Installation for Users

You'll need Java 21 and Maven 3.9+ to build Anserini.
Clone our repo then build with `bin/build.sh`.
The script is just a wrapper around Maven:

```bash
mvn clean package
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

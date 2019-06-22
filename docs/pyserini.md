# Pyserini: Anserini Integration with Python

Anserini was designed with Python integration in mind, for connecting with popular deep learning toolkits such as PyTorch. This is accomplished via [pyjnius](https://github.com/kivy/pyjnius). The `SimpleSearcher` class provides a simple Python/Java bridge, shown below:

```
import sys
sys.path += ['src/main/python']
from pyjnius_setup import configure_classpath
configure_classpath()

from jnius import autoclass
JString = autoclass('java.lang.String')
JSearcher = autoclass('io.anserini.search.SimpleSearcher')

searcher = JSearcher(JString('lucene-index.robust04.pos+docvectors+rawdocs'))
hits = searcher.search(JString('hubble space telescope'))

# the docid of the 1st hit
hits[0].docid

# the internal Lucene docid of the 1st hit
hits[0].ldocid

# the score of the 1st hit
hits[0].score

# the full document of the 1st hit
hits[0].content
```

Optionally, a path to Anserini root directory can be specified for scripts outside of Anserini:

```
anserini_root = {path/to/anserini}

import os, sys
sys.path += [os.path.join(anserini_root, 'src/main/python')]

from pyjnius_setup import configure_classpath
configure_classpath(anserini_root)
...
```


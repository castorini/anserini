import jnius_config
jnius_config.set_classpath("target/anserini-0.1.1-SNAPSHOT-fatjar.jar")

from jnius import autoclass
JSearcher = autoclass('io.anserini.search.SimpleSearcher')

print('Python Interface Integration Test Success')

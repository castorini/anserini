import sys
import jnius_config

jnius_config.set_classpath('target/' + sys.argv[1])

from jnius import autoclass
JSearcher = autoclass('io.anserini.search.SimpleSearcher')

print('Python Interface Integration Test Success')

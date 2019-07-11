import sys
sys.path += ['src/main/python']
from pyserini.setup import configure_classpath
configure_classpath()

from jnius import autoclass
JSearcher = autoclass('io.anserini.search.SimpleSearcher')

print('Python Interface Integration Test Success')

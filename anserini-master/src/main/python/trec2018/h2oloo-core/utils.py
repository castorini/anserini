import tarfile

class TfidfTxtReader(object):
  """

  """
  def __init__(self, filepath):
    self._file = open(filepath, 'r')
    self._curdoc = None
    self._curtfidf = (None, None)

  def hasnextdoc(self):
    self._curdoc = self._file.readline()
    if self._curdoc == '':
      self._file.close()
      return False
    return True

  def getnextdoc(self):
    return self._curdoc.strip()

  def skipdoc(self):
    while True:
      line = self._file.readline()
      if line == '\n' or line == '':
        break

  def hasnexttfidf(self, isalpha=True):
    while True:
      line = self._file.readline()
      if line == '\n' or line == '':
        return False
      
      word, tfidf = line.strip().split()
      if not isalpha or word.isalpha():
        self._curtfidf = (word, float(tfidf))
        return True
    
    # return self.hasnexttfidf(isalpha)

  def getnexttfidf(self):
    return self._curtfidf


class TfidfTgzReader(object):
  """

  """
  def __init__(self, filepath):
    self._file = tarfile.open(filepath, "r:gz")
    self._next = None
    self._curdoc = None
    self._curtfidf = (None, None)

  def hasnextdoc(self):
    self._next = self._file.next()
    if not self._next:
      self._file.close()
      return False
    if not self._next.isfile():
      return self.hasnextdoc()
    self._curdoc = self._file.extractfile(self._next)

    return True

  def getnextdoc(self):
    return self._next.name

  def skipdoc(self):
    return

  def hasnexttfidf(self):
    line = self._curdoc.readline()
    if not line:
      return False
    if line[:4] == b'<DOC':
      return self.hasnexttfidf()
    
    word, tfidf = line.decode('utf-8').strip().split(' ')
    self._curtfidf = (word, float(tfidf))
    return True

  def getnexttfidf(self):
    return self._curtfidf
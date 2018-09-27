"""
Anserini: A toolkit for reproducible information retrieval research built on Lucene

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

class _ParaSegmenter(object):
    """ The base class for all Paragraph Segmentation class """
    def __init__(self, bufferedreader, start_pattern_list=None):
        self._br = bufferedreader
        self._isstart = False
        self._curline = None
        self._paralist = []
        self._setup(start_pattern_list)

    def _setup(self, pattern_list):
        """ Find the start of the first paragraph of input document
        This can only be called once when initializing the object

        Args:
            pattern_list(list): an indicator of starting a paragraph

        Effect:
            self._isstart set to True if found, else remains False
            If <code>pattern_list</code> is None, treated as started
        """
        if pattern_list is None:
            self._isstart = True
            return

        while True:
            self._curline = self._br.readline()
            if not self._curline:
                return

            if self._curline in pattern_list:
                self._isstart = True
                return

    def _isend(self, line):
        """ An indicator of the a paragraph's end. The code in this base class indicates
        the end of a documents, as the end of a document indicates the end of a paragraph as well.

        Args:
            line(str): the line to be tested on

        Return:
            (bool): if this line is a paragraph end
        """
        if not line or line == b'</TEXT>':
            return True

        return False

    def hasnextpara(self):
        """ Check if there is a paragraph in this document

        If self._isstart == False after initialization, no useful information is contained in
        this document, then return False
        If reach the end of the doc, return False

        Return:
            (bool) if this document has further content

        """
        if not self._isstart or not self._curline or self._curline == b'</TEXT>':
            return False
        return True

    def nextpara(self):
        """ Two cases here:

        1. There is a pattern indicating a new paragraph followed by self._curline,
            In this case, after calling self.hasnextpara(), len(self._paralist) == 0.
            In this case, also, subclasses should fill self._paralist in `self.hasnextpara()`
        2. There is only a pattern indicating the end of a paragraph,
            so one has to readline until the end to see if it is a paragraph.
            In this case, after calling self.hasnextpara() and return True,
            len(self._paralist) > 0

        Return:
            str: A string contains a paragraph
        """
        if not self._paralist:
            while True:
                self._curline = self._br.readline()
                if self._isend(self._curline):
                    break
                self._paralist.append(self._curline.decode('utf-8').strip())

        parastr = ' '.join(self._paralist)
        del self._paralist[:]
        return parastr


class FBISParaSegmenter(_ParaSegmenter):
    """ A Segmenter to segment documents in FBIS collection under Robust04.

    Args:
        bufferedreader (io.BufferedReader): the buffered reader of a document.
    """
    def __init__(self, bufferedreader):
        start_pattern_list = [b'\n'] # start pattern by observation

        super(FBISParaSegmenter, self).__init__(bufferedreader, start_pattern_list)
        self._linelimit = 50 # An empirical number to decide if this is end of paragraph

    def _isend(self, line):
        if super(FBISParaSegmenter, self)._isend(line):
            return True

        if line[-2:] == b'.\n' and len(line) < self._linelimit:
            return True

        return False

    def hasnextpara(self):
        if not super(FBISParaSegmenter, self).hasnextpara():
            return False

        while True:
            self._curline = self._br.readline()
            if self._isend(self._curline):
                break
            self._paralist.append(self._curline.decode('utf-8').strip())

        if not self._paralist:
            # Handle the following pattern
            # b'\n'
            # b'</Text>\n'
            return False

        if len(self._curline) > 1:
            # skip if self._curline == b'\n'
            self._paralist.append(self._curline.decode('utf-8').strip())

        return True


class FR94ParaSegmenter(_ParaSegmenter):
    """ A Segmenter to segment documents in FR94 collection under Robust04.

    Args:
        bufferedreader (io.BufferedReader): the buffered reader of a document.
    """
    def __init__(self, bufferedreader):
        self._start_pattern_list = [
            b'<!-- PJG 0012 frnewline -->\n',
            b'<!-- PJG ITAG l=11 g=1 f=1 -->\n'
        ] # start pattern by observation
        super(FR94ParaSegmenter, self).__init__(bufferedreader, self._start_pattern_list)

    def _isend(self, line):
        if super(FR94ParaSegmenter, self)._isend(line):
            return True
        if line[:4] == b'<!--':
            return True
        return False

    def hasnextpara(self):
        if not self._isstart:
            return False

        # find start pattern
        while self._curline:
            if self._curline in self._start_pattern_list:
                return True
            self._curline = self._br.readline()

        return False


class FTParaSegmenter(_ParaSegmenter):
    """ A Segmenter to segment documents in FT collection under Robust04.

    Args:
        bufferedreader (io.BufferedReader): the buffered reader of a document.
    """
    def __init__(self, bufferedreader):
        start_pattern_list = [b'<TEXT>\n'] # start pattern by observation
        super(FTParaSegmenter, self).__init__(bufferedreader, start_pattern_list)
        self._linelimit = 105 # An empirical number to decide if this is end of paragraph

    def _isend(self, line):
        if super(FTParaSegmenter, self)._isend(line):
            return True
        if line[-2:] == b'.\n' and len(line) < self._linelimit:
            return True
        return False

    def hasnextpara(self):
        if not super(FTParaSegmenter, self).hasnextpara():
            return False

        while True:
            self._curline = self._br.readline()
            self._paralist.append(self._curline.decode('utf-8').strip())
            if self._isend(self._curline):
                break

        return True


class LAParaSegmenter(_ParaSegmenter):
    """ A Segmenter to segment documents in LA collection under Robust04.

    Args:
        bufferedreader (io.BufferedReader): the buffered reader of a document.
    """
    def __init__(self, bufferedreader):
        self._start_pattern_list = [b'<P>\n']
        super(LAParaSegmenter, self).__init__(bufferedreader, self._start_pattern_list)

    def _isend(self, line):
        if super(LAParaSegmenter, self)._isend(line):
            return True
        if line == b'</P>\n':
            return True
        return False

    def hasnextpara(self):
        if not super(LAParaSegmenter, self).hasnextpara():
            return False

        while self._curline:
            if self._curline in self._start_pattern_list:
                return True
            self._curline = self._br.readline()

        return False



class NYTParaSegmenter(_ParaSegmenter):
    """ A Segmenter to segment documents in New York Times collection under Core17.

    Args:
        bufferedreader (io.BufferedReader): the buffered reader of a document.
    """
    def __init__(self, bufferedreader):
        super(NYTParaSegmenter, self).__init__(bufferedreader)

    def hasnextpara(self):
        self._curline = self._br.readline()
        return len(self._curline) > 0

    def nextpara(self):
        return self._curline.decode('utf-8').strip()

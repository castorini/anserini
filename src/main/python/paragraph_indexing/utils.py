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

import os
import tarfile

class TgzReader(object):
    """ A Reader to read tar.gz with multiple raw document files efficiently.

    A tar.gz file has the following structure:

        DOCID_1:
            RAW DOCS 1
        DOCID_2:
            RAW DOCS 2
        ...

    Args:
        path(str): the path of input tar.gz file
    """
    def __init__(self, path):
        self._path = path
        self._tar = tarfile.open(path, "r:gz")
        self._next = None

    def close(self):
        """ close the reader stream. """
        self._tar.close()

    def hasnext(self):
        """ return whether the tar file has files or not

        Returns:
            bool: whether the tar file has files or not
        """
        self._next = self._tar.next()
        if not self._next:
            return False
        if not self._next.isfile():
            return self.hasnext()
        return True

    def next(self):
        """ get the next tf-idf files

        Returns:
            (str, io.BytesIO): the file name and a bytes io stream containing doc contents.

        Raises:
            ValueError: if there is not next entry in this tar file.
        """
        if self._next:
            return self._next.name, self._tar.extractfile(self._next)
        raise ValueError("No files.")

def safe_mkdir(path):
    """ create directory if not exists """
    if not os.path.exists(path):
        os.mkdir(path)

def form_json(doc_name, para_id, content):
    """ form a document into json format """
    doc = {
        'id': '{}.{:04d}'.format(doc_name, para_id),
        'contents': content
    }
    return doc

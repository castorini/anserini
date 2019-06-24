# -*- coding: utf-8 -*-
#
# Anserini: A toolkit for reproducible information retrieval research built on Lucene
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""
Module for providing python interface to Anserini searchers
"""

from ..pyclass import JSearcher, JString

import logging
logger = logging.getLogger(__name__)

class SimpleSearcher:
    
    def __init__(self, index_dir):
        self.index = index_dir
        self.object = JSearcher(JString(index_dir))
    
    def search(self, *args):
        if (len(args) == 1 and 
            isinstance(args[0], str)):
            return self.object.search(JString(args[0]))
        elif (len(args) == 2 and isinstance(args[0], str)):
            return self.object.search(JString(args[0]), args[1])
        elif (len(args) == 3 and isinstance(args[0], str)):
            return self.object.search(JString(args[0]), args[1], args[2])
        else:
            raise ValueError("Unsupported arguments for search.")
    
            

        
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
Module for providing python interface to Anserini generators
"""

from ..pyclass import JIndexHelpers, JGenerators

import logging
logger = logging.getLogger(__name__)

class Generator:
        
    def __init__(self, generator_class):
        self.counters = JIndexHelpers.JCounters()
        self.args = JIndexHelpers.JArgs()
        self.generator_class = generator_class
        self.object = self._get_generator()
    
    def _get_generator(self):
        try:
            return JGenerators[self.generator_class].value(self.args, self.counters)
        except:
            raise ValueError(self.generator_class)
            
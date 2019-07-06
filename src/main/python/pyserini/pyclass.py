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

'''
Module for hiding Python-Java calls via Pyjnius
'''

### Pyjnius setup

from .setup import configure_classpath, os
# find path back to root of anserini directory
configure_classpath(os.path.abspath(
        os.path.join(os.path.realpath(__file__), '../../../../..')))
    
from jnius import autoclass, cast
from enum import Enum

### Java

JString = autoclass('java.lang.String')
JPath = autoclass('java.nio.file.Path')
JPaths = autoclass('java.nio.file.Paths')
JList = autoclass('java.util.List')
JArrayList = autoclass('java.util.ArrayList')

### Search

JSearcher = autoclass('io.anserini.search.SimpleSearcher')

### Generator

class JIndexHelpers:
    
    def JArgs():
        args = autoclass('io.anserini.index.IndexCollection$Args')()
        args.storeRawDocs = True ## to store raw text as an option
        args.dryRun = True ## So that indexing will be skipped
        return args
    
    def JCounters():
        IndexCollection = autoclass('io.anserini.index.IndexCollection')
        Counters = autoclass('io.anserini.index.IndexCollection$Counters')
        return Counters(IndexCollection)

class JGenerators(Enum):        
    LuceneDocumentGenerator = autoclass('io.anserini.index.generator.LuceneDocumentGenerator')
    JsoupGenerator = autoclass('io.anserini.index.generator.JsoupGenerator')
    NekoGenerator = autoclass('io.anserini.index.generator.NekoGenerator')
    TweetGenerator = autoclass('io.anserini.index.generator.TweetGenerator')
    WapoGenerator = autoclass('io.anserini.index.generator.WapoGenerator')

### Collection
        
class JCollections(Enum):
    CarCollection = autoclass('io.anserini.collection.CarCollection')
    ClueWeb09Collection = autoclass('io.anserini.collection.ClueWeb09Collection')
    ClueWeb12Collection = autoclass('io.anserini.collection.ClueWeb12Collection')
    HtmlCollection = autoclass('io.anserini.collection.HtmlCollection')
    JsonCollection = autoclass('io.anserini.collection.JsonCollection')
    NewYorkTimesCollection = autoclass('io.anserini.collection.NewYorkTimesCollection')
    TrecCollection = autoclass('io.anserini.collection.TrecCollection')
    TrecwebCollection = autoclass('io.anserini.collection.TrecwebCollection')
    TweetCollection = autoclass('io.anserini.collection.TweetCollection')
    WashingtonPostCollection = autoclass('io.anserini.collection.WashingtonPostCollection')
    WikipediaCollection = autoclass('io.anserini.collection.WikipediaCollection')


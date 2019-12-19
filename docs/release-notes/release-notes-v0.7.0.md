# Anserini Release Notes (v0.7.0)

**Release date: December 13, 2019**

### New Features

+ Upgraded to Lucene 8.3.0.
+ Added basic support for indexing and search in non-English languages.
+ Added regressions for NTCIR-8 (Chinese), CLEF 2006 (French), TREC 2002 (Arabic), and FIRE 2012 (Bengali, Hindi, English).
+ Added instructions and regressions for docTTTTTquery on MS MARCO Passage Retrieval task.
+ Added initial support for indexing CORE open access research papers.
+ Added similarity that accurately computes document lengths for BM25.
+ Added support for approximate nearest-neighbor search (see arXiv:1910.10208).
+ Added ability to read topics directly from the fatjar.
+ Improved support for Elasticsearch.
+ Improved support for Solr.

### Pyserini

+ Removed Pyserini from repo, now standalone project.
+ Refactored SimpleSearcher in coordination with initial Pyserini release.
+ Added IndexReaderUtils, exposing various hooks for Pyserini.

### Cleanup, Testing, Documentation

+ Added integration with Codecov.
+ Added more test cases to increase test coverage.
+ Added links to Anserini notebooks.
+ Added end-to-end integration tests for Solr and Elasticsearch.
+ Moved CACM collection into repo for cleaner, more self-contained testing.
+ Refactored logging and counters in indexer, improved documentation of options.
+ Cleaned up warnings during build.
+ Slimmed down fatjar size, removed unnecessary dependencies.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Ryan Clancy ([r-clancy](https://github.com/r-clancy))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Weihua Li ([w329li](https://github.com/w329li))
+ Alireza Mirzaeiyan ([alirezamirzaeiyan](https://github.com/alirezamirzaeiyan))
+ Kelvin Jiang ([infinitecold](https://github.com/infinitecold))
+ Leonid Boytsov ([searchivarius](https://github.com/searchivarius))
+ Maik Fröbe ([mam10eks](https://github.com/mam10eks))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Zhaohao Zeng ([matthew-z](https://github.com/matthew-z))

## All Contributors

Sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Ryan Clancy ([r-clancy](https://github.com/r-clancy))
+ Ahmet Arslan ([iorixxx](https://github.com/iorixxx))
+ Royal Sequiera ([rosequ](https://github.com/rosequ))
+ Emily Wang ([emmileaf](https://github.com/emmileaf))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Salman Mohammed ([Salman Mohammed](https://github.com/salman1993))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Xin Qian ([xeniaqian94](https://github.com/xeniaqian94))
+ Adam Roegiest ([aroegies](https://github.com/aroegies))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Weihua Li ([w329li](https://github.com/w329li))
+ Toke Eskildsen ([tokee](https://github.com/tokee))
+ Zhaohao Zeng ([matthew-z](https://github.com/matthew-z))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Xing Niu ([xingniu](https://github.com/xingniu))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Mina Farid ([minafarid](https://github.com/minafarid))
+ Mengfei Liu ([meng-f](https://github.com/meng-f))
+ Adrien Grand ([jpountz](https://github.com/jpountz))
+ Gaurav Baruah ([gauravbaruah](https://github.com/gauravbaruah))
+ Edward Lu ([edwardhdlu](https://github.com/edwardhdlu))
+ Adrien Pouyet ([Ricocotam](https://github.com/Ricocotam))
+ Ruifan Yu ([tiddler](https://github.com/tiddler))
+ Leonid Boytsov ([searchivarius](https://github.com/searchivarius))
+ Petek Yıldız ([ptkyldz](https://github.com/ptkyldz))
+ Maik Fröbe ([mam10eks](https://github.com/mam10eks))
+ Matt Yang ([justram](https://github.com/justram))
+ Kelvin Jiang ([infinitecold](https://github.com/infinitecold))
+ Charles Wu ([charW](https://github.com/charW))
+ Matteo Catena ([catenamatteo](https://github.com/catenamatteo))
+ Andrew Yates ([andrewyates](https://github.com/andrewyates))
+ Antonio Mallia ([amallia](https://github.com/amallia))
+ Alireza Mirzaeiyan ([alirezamirzaeiyan](https://github.com/alirezamirzaeiyan))
+ Horatiu Lazu ([MathBunny](https://github.com/MathBunny))

# Anserini Release Notes (v0.15.0)

**Release date: September 22, 2022**

+ Upgraded to Lucene 9.3.0!
+ Upgraded to `fastutil` 8.5.8 and fixed longstanding `FeatureVector` issue ([#840](https://github.com/castorini/anserini/issues/840)).
+ Upgraded to `jsoup` 1.15.3 to fix security vulnerability.
+ Refactored searcher to check for Lucene 8 indexes; if detected, disables consistent tie-breaking. Workaround for Lucene 8/9 index incompatibility ([#1952](https://github.com/castorini/anserini/issues/1952)).
+ Refactored Pyserini bindings; changed method names to `snake_case`.
+ Removed Elasticsearch and Solr code paths.
+ Added missing relevance feedback conditions to MS MARCO V2 regressions.
+ Added initial bindings to Lucene HNSW indexes.
+ Added missing topics and qrels.
+ Added note about inability to exactly reproduce TREC-COVID runs (due to Lucene 9 upgrade).
+ Cleaned up deprecated code (e.g., `SearchMsmarco`).
+ Updated all regressions to reflect changes in code.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Aileen Lin ([AileenLin](https://github.com/AileenLin))
+ Ogundepo Odunayo ([ToluClassics](https://github.com/ToluClassics))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))

## All Contributors

All contributors with five or more commits, sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Ahmet Arslan ([iorixxx](https://github.com/iorixxx))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Emily Wang ([emmileaf](https://github.com/emmileaf))
+ Royal Sequiera ([rosequ](https://github.com/rosequ))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Ogundepo Odunayo ([ToluClassics](https://github.com/ToluClassics))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Matt Yang ([justram](https://github.com/justram))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Adam Yang ([adamyy](https://github.com/adamyy))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Manveer Tamber ([manveertamber](https://github.com/manveertamber))
+ Matt Yang ([d1shs0ap](https://github.com/d1shs0ap))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Xinyu (Crystina) Zhang ([crystina-z](https://github.com/crystina-z))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Yuqing Xie ([amyxie361](https://github.com/amyxie361))
+ Nandan Thakur ([thakur-nandan](https://github.com/thakur-nandan))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Ryan Clancy ([ryan-clancy](https://github.com/ryan-clancy))

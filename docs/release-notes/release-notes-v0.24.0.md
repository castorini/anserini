# Anserini Release Notes (v0.24.0)

+ **Release date:** December 28, 2023
+ **Lucene version:** Lucene 9.9.1

## Summary of Changes

+ Upgraded to Lucene 9.9.1:
  + Refactored code to use latest codecs.
  + Added regressions to take advantage of HNSW `int8` quantization (but doesn't work for OpenAI Ada2).
+ Major refactoring of search code paths:
  + Refactored to create `HnswDenseSearcher` and `InvertedDenseSearcher` to provide Python bindings.
  + Refactored `SearchHnswDenseVectors` and `SearchInvertedDenseVectors` as wrappers to provide main entry points to above searchers.
  + Improved alignment between `SearchCollection` and dense vector search classes above, extracting common code into helper classes.
  + Aligned `ScoredDoc` and `ScoredDocs` (was previously `ScoredDocuments`) as container objects for Lucene results.
  + Refactored searchers to use `ScoredDoc` instead of class-specific `Result` objects.
  + Imposed uniform camelCasing in search args.
+ Major refactoring of indexing pipelines:
  + Extracted common code paths from `IndexCollection`, `IndexHnswDenseVectors`, and `IndexInvertedDenseVectors` into `AbstractIndexer`.
  + Imposed uniform camelCasing in index args.
+ Improved test coverage.
+ Added basic ability to download pre-built indexes.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Matt Yang ([justram](https://github.com/justram))
+ Andre Slavescu ([AndreSlavescu](https://github.com/AndreSlavescu))
+ Areel Ullah Khan ([AreelKhan](https://github.com/AreelKhan))
+ Arthur Chen ([ArthurChen189](https://github.com/ArthurChen189))
+ Chris Hegarty ([ChrisHegarty](https://github.com/ChrisHegarty))
+ Golnoosh Asefi ([golnooshasefi](https://github.com/golnooshasefi))
+ Minhajul Abedin ([Minhajul99](https://github.com/Minhajul99))
+ Paniz Ojaghi ([Panizghi](https://github.com/Panizghi))
+ Sahar ([saharsamr](https://github.com/saharsamr))
+ Yifei Li ([tudou0002](https://github.com/tudou0002))
+ alimansouri ([alimt1992](https://github.com/alimt1992))
+ kdricci ([kdricci](https://github.com/kdricci))
+ ljk423 ([ljk423](https://github.com/ljk423))
+ Yahya Jabary ([sueszli](https://github.com/sueszli))

## All Contributors

All contributors with five or more commits, sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Ogundepo Odunayo ([ToluClassics](https://github.com/ToluClassics))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Ahmet Arslan ([iorixxx](https://github.com/iorixxx))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Arthur Chen ([ArthurChen189](https://github.com/ArthurChen189))
+ Emily Wang ([emmileaf](https://github.com/emmileaf))
+ Royal Sequiera ([rosequ](https://github.com/rosequ))
+ Matt Yang ([justram](https://github.com/justram))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Xinyu (Crystina) Zhang ([crystina-z](https://github.com/crystina-z))
+ Adam Yang ([adamyy](https://github.com/adamyy))
+ Manveer Tamber ([manveertamber](https://github.com/manveertamber))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Akintunde Oladipo ([theyorubayesian](https://github.com/theyorubayesian))
+ Matt Yang ([d1shs0ap](https://github.com/d1shs0ap))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Mofe Adeyemi ([Mofetoluwa](https://github.com/Mofetoluwa))
+ Aileen Lin ([AileenLin](https://github.com/AileenLin))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Yuqing Xie ([amyxie361](https://github.com/amyxie361))
+ Nandan Thakur ([thakur-nandan](https://github.com/thakur-nandan))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Ryan Clancy ([ryan-clancy](https://github.com/ryan-clancy))

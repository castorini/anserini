# Anserini Release Notes (v0.22.0)

+ **Release date:** August 28, 2023
+ **Lucene version:** Lucene 9.5.0

## Summary of Changes

+ Upgraded to Lucene 9.5.0.
+ Refactored code to improve support for HNSW and FW/LexLSH:
  + `IndexCollection`, `IndexHnswDenseVectors`, and `IndexInvertedDenseVectors`.
  + `SearchCollection`, `SearchHnswDenseVectors`, and `SearchInvertedDenseVectors`.
+ Added ONNX support, including hooks in `SimpleImpactSearcher` for Pyserini.
+ Added ability to parse raw text "on-the-fly" to enable relevance feedback in `SimpleImpactSearcher`.
+ Added regressions for HNSW in Lucene using cos DPR distil.
+ Added regressions for SPLADE++ with ONNX.
+ Fixed misalignment in `SearchCollection` and `SimpleImpactSearcher` implementations.
+ Created documentation for indexing OpenAI `ada2` embeddings with Lucene HNSW.
+ Refactored and improved documentation, including onboarding path.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Arthur Chen ([ArthurChen189](https://github.com/ArthurChen189))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Aileen Lin ([AileenLin](https://github.com/AileenLin))
+ Sahel Sharify ([sahel-sh](https://github.com/sahel-sh))
+ Aryaman Gupta ([aryamancodes](https://github.com/aryamancodes))
+ Bill Cui ([billcui57](https://github.com/billcui57))
+ Carlos Eduardo Rosar Kós Lassance ([cadurosar](https://github.com/cadurosar))
+ Jason Zhang ([yilinjz](https://github.com/yilinjz))
+ Morteza Behbooei ([mobehbooei](https://github.com/mobehbooei))
+ Zoe Zou ([zoehahaha](https://github.com/zoehahaha))
+ Andrwyl ([Andrwyl](https://github.com/Andrwyl))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Jasper Xian ([jasper-xian](https://github.com/jasper-xian))
+ Jocn2020 ([Jocn2020](https://github.com/Jocn2020))
+ Mofe Adeyemi ([Mofetoluwa](https://github.com/Mofetoluwa))
+ Richard Fan ([Richard5678](https://github.com/Richard5678))
+ Singularity-tian ([Singularity-tian](https://github.com/Singularity-tian))
+ Tailai Wang ([tailaiwang](https://github.com/tailaiwang))
+ Ygor Gallina ([ygorg](https://github.com/ygorg))
+ pratyushpal ([pratyushpal](https://github.com/pratyushpal))

## All Contributors

All contributors with five or more commits, sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Ogundepo Odunayo ([ToluClassics](https://github.com/ToluClassics))
+ Ahmet Arslan ([iorixxx](https://github.com/iorixxx))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Emily Wang ([emmileaf](https://github.com/emmileaf))
+ Royal Sequiera ([rosequ](https://github.com/rosequ))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Matt Yang ([justram](https://github.com/justram))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Arthur Chen ([ArthurChen189](https://github.com/ArthurChen189))
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
+ Aileen Lin ([AileenLin](https://github.com/AileenLin))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Yuqing Xie ([amyxie361](https://github.com/amyxie361))
+ Nandan Thakur ([thakur-nandan](https://github.com/thakur-nandan))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Ryan Clancy ([ryan-clancy](https://github.com/ryan-clancy))

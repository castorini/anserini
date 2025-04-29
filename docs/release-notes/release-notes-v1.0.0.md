# Anserini Release Notes (v1.0.0)

+ **Release date:** April 25, 2025
+ **Lucene version:** Lucene 9.9.1

## Summary of Changes

**Release to coincide with SIGIR 2025 paper**

+ Added bindings for SPLADEv3 on MS MARCO passage.
+ Added `ArcticEmbedLEncoder` ONNX implementation.
+ Added ArcticEmbedL regressions for MS MARCO V2.1 RAG 24 + prebuilt indexes.
+ Added `vector()` method to `SourceDocument` interface.
+ Added HNSW support to the localhost REST API.
+ Added implementation of parallel search on shards.
+ Refactored `DocumentGenerators`.
+ Refactored fusion implementation to use `ScoredDocs`; other improvements.
+ Refactored all ONNX encoder implementations and tests.
+ Refactored `IndexInfo` naming.
+ Renamed regression tests to reduce confusion; removed JSON dense vector regressions.
+ Removed `msmarco-v2-doc-segmented.unicoil-0shot` regressions + related variants.
+ Removed LTR code.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Vincent ([vincent-4](https://github.com/vincent-4))
+ Lily Ge ([lilyjge](https://github.com/lilyjge))
+ Brayden Zhong ([b8zhong](https://github.com/b8zhong))
+ Daniel Guo ([clides](https://github.com/clides))
+ JJGreen0 ([JJGreen0](https://github.com/JJGreen0))
+ Shivani Upadhyay ([UShivani3](https://github.com/UShivani3))
+ Alexisfine ([Alexisfine](https://github.com/Alexisfine))
+ Asghar Taqvi ([Taqvis](https://github.com/Taqvis))
+ Erfan Sadraiye ([erfansadraiye](https://github.com/erfansadraiye))
+ Jie Min ([Stefan824](https://github.com/Stefan824))
+ Jonathan Zhao ([jazyz](https://github.com/jazyz))
+ m0v3np1ck ([ricky42613](https://github.com/ricky42613))
+ Mohammaderfan Kabir ([mohammaderfankabir](https://github.com/mohammaderfankabir))
+ Zafar Erkinboev ([ezafar](https://github.com/ezafar))

## All Contributors

All contributors with five or more commits, sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Ogundepo Odunayo ([ToluClassics](https://github.com/ToluClassics))
+ Arthur Chen ([ArthurChen189](https://github.com/ArthurChen189))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Ahmet Arslan ([iorixxx](https://github.com/iorixxx))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Royal Sequiera ([rosequ](https://github.com/rosequ))
+ Emily Wang ([emmileaf](https://github.com/emmileaf))
+ Jheng-Hong Yang ([justram](https://github.com/justram))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Eric Zhang ([16BitNarwhal](https://github.com/16BitNarwhal))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Vincent ([vincent-4](https://github.com/vincent-4))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Jasper Xian ([jasper-xian](https://github.com/jasper-xian))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Adam Yang ([adamyy](https://github.com/adamyy))
+ Mofe Adeyemi ([Mofetoluwa](https://github.com/Mofetoluwa))
+ Xinyu (Crystina) Zhang ([crystina-z](https://github.com/crystina-z))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Manveer Tamber ([manveertamber](https://github.com/manveertamber))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Akintunde Oladipo ([theyorubayesian](https://github.com/theyorubayesian))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Matt Yang ([matthewyryang](https://github.com/matthewyryang))
+ Shivani Upadhyay ([UShivani3](https://github.com/UShivani3))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Lily Ge ([lilyjge](https://github.com/lilyjge))
+ Brayden Zhong ([b8zhong](https://github.com/b8zhong))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Aileen Lin ([AileenLin](https://github.com/AileenLin))
+ Ryan Clancy ([ryan-clancy](https://github.com/ryan-clancy))
+ Vivek Alamuri ([valamuri2020](https://github.com/valamuri2020))
+ Yuqing Xie ([amyxie361](https://github.com/amyxie361))
+ Nandan Thakur ([thakur-nandan](https://github.com/thakur-nandan))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))

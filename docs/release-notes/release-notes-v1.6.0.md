# Anserini Release Notes (v1.6.0)

+ **Release date:** February 24, 2026
+ **Lucene version:** Lucene 9.9.1

## Summary of Changes

+ Reorganized metadata for prebuilt indexes, refactoring `IndexInfo` into separate classes in package `io.anserini.index.prebuilt`.
+ Refactored regressions into `RunRegressionsFromCorpus` and `RunRegressionsFromPrebuiltIndexes`.
+ Refactored `trec_eval` and created `TrecEval`.
+ Added CACM regressions.
+ Added topics and qrels for MMEB and DSE.
+ Cleaned up logging output, suppressing unnecessary logging: added bridge from JUL to Slf4j bindings.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Daniel Zhang ([zdann15](https://github.com/zdann15))
+ Aaryan Shroff ([aaryanshroff](https://github.com/aaryanshroff))
+ FayizMohideen ([FayizMohideen](https://github.com/FayizMohideen))
+ HusamIsied ([HusamIsied](https://github.com/HusamIsied))
+ Izzat ([izzat5233](https://github.com/izzat5233))
+ Lingwei Gu ([lingwei-gu](https://github.com/lingwei-gu))
+ Mohammed Maher ([maherapp](https://github.com/maherapp))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Sahel Sharifymoghaddam ([sahel-sh](https://github.com/sahel-sh))
+ zizimind ([zizimind](https://github.com/zizimind))

## All Contributors

All contributors with five or more commits, sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Lily Ge ([lilyjge](https://github.com/lilyjge))
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
+ Eric Zhang ([16BitNarwhal](https://github.com/16BitNarwhal))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Vincent ([vz-gh](https://github.com/vz-gh))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Daniel Guo ([clides](https://github.com/clides))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Jasper Xian ([jasper-xian](https://github.com/jasper-xian))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Adam Yang ([adamyy](https://github.com/adamyy))
+ Mofe Adeyemi ([Mofetoluwa](https://github.com/Mofetoluwa))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Xinyu (Crystina) Zhang ([crystina-z](https://github.com/crystina-z))
+ Manveer Tamber ([manveertamber](https://github.com/manveertamber))
+ Shivani Upadhyay ([UShivani3](https://github.com/UShivani3))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Matt Yang ([matthewyryang](https://github.com/matthewyryang))
+ FarmersWrap ([FarmersWrap](https://github.com/FarmersWrap))
+ Akintunde Oladipo ([theyorubayesian](https://github.com/theyorubayesian))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Suraj Subrahmanyan ([suraj-subrahmanyan](https://github.com/suraj-subrahmanyan))
+ Brayden Zhong ([b8zhong](https://github.com/b8zhong))
+ JJGreen0 ([JJGreen0](https://github.com/JJGreen0))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Aileen Lin ([AileenLin](https://github.com/AileenLin))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Yuqing Xie ([amyxie361](https://github.com/amyxie361))
+ Nandan Thakur ([thakur-nandan](https://github.com/thakur-nandan))
+ Sahel Sharifymoghaddam ([sahel-sh](https://github.com/sahel-sh))
+ Ryan Clancy ([ryan-clancy](https://github.com/ryan-clancy))
+ Lingwei Gu ([lingwei-gu](https://github.com/lingwei-gu))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Steven Chen ([wu-ming233](https://github.com/wu-ming233))
+ Vivek Alamuri ([valamuri2020](https://github.com/valamuri2020))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))

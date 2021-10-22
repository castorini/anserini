# Anserini Release Notes (v0.13.4)

**Release date: October 22, 2021**

+ Fixed score overflow issue in `ScoreTiesAdjusterReranker` for impact scoring (bug manifested by SPLADEv2).
+ Fixed MS MARCO V2 corpus naming conventions.
+ Added regressions for MS MARCO V2 passage/document ranking tasks. 
+ Added ability to read queries from compressed files.
+ Added/organized regressions for sparse learned retrieval models: DeepImpact, uniCOIL (doc2query-T5 and TILDE), SPLADEv2.
+ Added test cases for `Qrels`.
+ Upgraded `jsoup` to address security vulnerabilities; resulted in minor changes to some regressions.
+ Improved `C4NoCleanCollection`.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Xuntian Lin ([apokali](https://github.com/apokali))
+ Arthur Chen ([ArthurChen189](https://github.com/ArthurChen189)) 
+ David Duan ([RootofalleviI](https://github.com/RootofalleviI))
+ Justin Leung ([leungjch](https://github.com/leungjch))
+ Ogundepo Odunayo ([ToluClassics](https://github.com/ToluClassics))
+ Shengyao Zhuang ([ArvinZhuang](https://github.com/ArvinZhuang))
+ Yuetong Wang ([AlexWang000](https://github.com/AlexWang000))

## All Contributors

All contributors with five or more commits, sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Ryan Clancy ([r-clancy](https://github.com/r-clancy))
+ Ahmet Arslan ([iorixxx](https://github.com/iorixxx))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Emily Wang ([emmileaf](https://github.com/emmileaf))
+ Royal Sequiera ([rosequ](https://github.com/rosequ))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Adam Yang ([adamyy](https://github.com/adamyy))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))

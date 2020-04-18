# Anserini Release Notes (v0.9.0)

**Release date: April 18, 2020**

+ Improved support for the [COVID-19 Open Research Dataset](https://pages.semanticscholar.org/coronavirus-research) (CORD-19).
+ Added `SimpleNearestNeighborSearcher`, exposing approximate nearest neighbor search capabilities in Pyserini.
+ Added support for [Trialstreamer](https://trialstreamer.robotreviewer.net/), exposed as new vertical in the [Neural Covidex](http://covidex.ai/).
+ Added `CovidTopicReader` topic reader and topics for [TREC-COVID](https://ir.nist.gov/covidSubmit/).
+ Added `Covid19QueryGenerator`, improved query generation for COVID-19 queries.
+ Refactored `SimpleSearcher` and `IndexReaderUtils` for better Pyserini support.
+ Refactored indexing pipeline based on revised contract of `contents()` and `raw()` in `SourceDocument`.
+ Refactored Core17 and Core18 regressions to reflect revised `SourceDocument` contract (above). Regressions values changed slightly, see [here](https://github.com/castorini/anserini/blob/master/docs/regressions-log.md#april-7-2020) and [here](https://github.com/castorini/anserini/blob/master/docs/regressions-log.md#april-12-2020).
+ Improved integration testing harness.
+ Added end-to-end integration tests for `AclAnthology` and `CoreCollection`.
+ Added initial test cases for CORD-19.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Yuqi Kiu ([yuki617](https://github.com/yuki617))
+ Kuang Lu ([lukuang](https://github.com/lukuang))

## All Contributors

Sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Ryan Clancy ([r-clancy](https://github.com/r-clancy))
+ Ahmet Arslan ([iorixxx](https://github.com/iorixxx))
+ Royal Sequiera ([rosequ](https://github.com/rosequ))
+ Emily Wang ([emmileaf](https://github.com/emmileaf))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Salman Mohammed ([Salman Mohammed](https://github.com/salman1993))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Xin Qian ([xeniaqian94](https://github.com/xeniaqian94))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Adam Roegiest ([aroegies](https://github.com/aroegies))
+ Weihua Li ([w329li](https://github.com/w329li))
+ Toke Eskildsen ([tokee](https://github.com/tokee))
+ Zhaohao Zeng ([matthew-z](https://github.com/matthew-z))
+ Yuqi Kiu ([yuki617](https://github.com/yuki617))
+ Xing Niu ([xingniu](https://github.com/xingniu))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Mina Farid ([minafarid](https://github.com/minafarid))
+ Mengfei Liu ([meng-f](https://github.com/meng-f))
+ Maik Fröbe ([mam10eks](https://github.com/mam10eks))
+ Adrien Grand ([jpountz](https://github.com/jpountz))
+ Gaurav Baruah ([gauravbaruah](https://github.com/gauravbaruah))
+ Edward Lu ([edwardhdlu](https://github.com/edwardhdlu))
+ Adrien Pouyet ([Ricocotam](https://github.com/Ricocotam))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Ruifan Yu ([tiddler](https://github.com/tiddler))
+ Leonid Boytsov ([searchivarius](https://github.com/searchivarius))
+ Petek Yıldız ([ptkyldz](https://github.com/ptkyldz))
+ Kevin Xu ([kevinxyc1](https://github.com/kevinxyc1))
+ Matt Yang ([justram](https://github.com/justram))
+ Kelvin Jiang ([infinitecold](https://github.com/infinitecold))
+ Guy Rosin ([guyrosin](https://github.com/guyrosin))
+ Charles Wu ([charW](https://github.com/charW))
+ Matteo Catena ([catenamatteo](https://github.com/catenamatteo))
+ Andrew Yates ([andrewyates](https://github.com/andrewyates))
+ Alireza Mirzaeiyan ([alirezamirzaeiyan](https://github.com/alirezamirzaeiyan))
+ Antonio Mallia ([amallia](https://github.com/amallia))
+ Horatiu Lazu ([MathBunny](https://github.com/MathBunny))
+ Edward Li ([LuKuuu](https://github.com/LuKuuu))

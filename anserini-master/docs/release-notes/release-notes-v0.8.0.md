# Anserini Release Notes (v0.8.0)

**Release date: March 11, 2020**

+ Added collection and Lucene generator support for CORE collections, bibtex collections, and the ACL Anthology.
+ Added basic Solrini support for CORE collections, bibtex collections, and the ACL Anthology.
+ Added regressions for the background linking task in the News Tracks from TREC 2018 and 2019.
+ Added regressions for the TREC 2019 Deep Learning Track.
+ Augmented methods in `SimpleSearcher` and `IndexReaderUtils` with support for a custom `Analyzer`.
+ Exposed additional hooks in `SimpleSearcher` and `IndexReaderUtils` for Pyserini.
+ Refactored initialization methods in the renamed `DefaultEnglishAnalyzer`.
+ Clarified documentation about what "regression tests" mean and introduced the Anserini replicability promise.
+ Gathered all Lucene field names from different locations into `IndexArgs`.
+ Improved the structure of unit test cases for collection classes.
+ Improved support for Solrini and Elasterini: both now support `robust04`, `core18`, and `msmarco-passage`.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))

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
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Xin Qian ([xeniaqian94](https://github.com/xeniaqian94))
+ Adam Roegiest ([aroegies](https://github.com/aroegies))
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
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Ruifan Yu ([tiddler](https://github.com/tiddler))
+ Leonid Boytsov ([searchivarius](https://github.com/searchivarius))
+ Petek Yıldız ([ptkyldz](https://github.com/ptkyldz))
+ Maik Fröbe ([mam10eks](https://github.com/mam10eks))
+ Kevin Xu ([kevinxyc1](https://github.com/kevinxyc1))
+ Matt Yang ([justram](https://github.com/justram))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Guy Rosin ([guyrosin](https://github.com/guyrosin))
+ Charles Wu ([charW](https://github.com/charW))
+ Matteo Catena ([catenamatteo](https://github.com/catenamatteo))
+ Andrew Yates ([andrewyates](https://github.com/andrewyates))
+ Alireza Mirzaeiyan ([amirzaeiyan](https://github.com/amirzaeiyan))
+ Antonio Mallia ([amallia](https://github.com/amallia))
+ Horatiu Lazu ([MathBunny](https://github.com/MathBunny))
+ Edward Li ([LuKuuu](https://github.com/LuKuuu))

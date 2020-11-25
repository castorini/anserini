# Anserini Release Notes (v0.9.2)

**Release date: May 14, 2020**

+ Updated support for CORD-19, up through data drop of 2020/05/12.
+ Untangled `SimpleTweetSearcher` from `SimpleSearcher`, with verification script to check outputs against `SearchCollection`.
+ Added support to compute score of a document with respect to a query.
+ Added miscellaneous util scripts for TREC-COVID.
+ Added method to expose total number of documents in `SimpleSearcher`.
+ Added option to use custom `QueryGenerator` in `SearchCollection`.
+ Exposed Lucene query building blocks for Pyserini.
+ Improved support to compute BM25 weight for a term in a document.
+ Cleaned up regression scripts (file locations, etc.).

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Alex Dou ([YimingDou](https://github.com/YimingDou))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Rohil Gupta ([rohilG](https://github.com/rohilG))
+ egzhbdt ([egzhbdt](https://github.com/egzhbdt))
+ Richard Xu ([richard3983](https://github.com/richard3983))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Alvis Wong ([wongalvis14](https://github.com/wongalvis14))

## All Contributors

Sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Ryan Clancy ([r-clancy](https://github.com/r-clancy))
+ Ahmet Arslan ([iorixxx](https://github.com/iorixxx))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Royal Sequiera ([rosequ](https://github.com/rosequ))
+ Emily Wang ([emmileaf](https://github.com/emmileaf))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Xin Qian ([xeniaqian94](https://github.com/xeniaqian94))
+ Adam Roegiest ([aroegies](https://github.com/aroegies))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Weihua Li ([w329li](https://github.com/w329li))
+ Toke Eskildsen ([tokee](https://github.com/tokee))
+ Zhaohao Zeng ([matthew-z](https://github.com/matthew-z))
+ Xing Niu ([xingniu](https://github.com/xingniu))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Mina Farid ([minafarid](https://github.com/minafarid))
+ Mengfei Liu ([meng-f](https://github.com/meng-f))
+ Maik Fröbe ([mam10eks](https://github.com/mam10eks))
+ Adrien Grand ([jpountz](https://github.com/jpountz))
+ Gaurav Baruah ([gauravbaruah](https://github.com/gauravbaruah))
+ Edward Lu ([edwardhdlu](https://github.com/edwardhdlu))
+ Adrien Pouyet ([Ricocotam](https://github.com/Ricocotam))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Vera Lin ([y276lin](https://github.com/y276lin))
+ Alvis Wong ([wongalvis14](https://github.com/wongalvis14))
+ Wei Pang ([weipang142857](https://github.com/weipang142857))
+ Ruifan Yu ([tiddler](https://github.com/tiddler))
+ Leonid Boytsov ([searchivarius](https://github.com/searchivarius))
+ Rohil Gupta ([rohilG](https://github.com/rohilG))
+ Richard Xu ([richard3983](https://github.com/richard3983))
+ Petek Yıldız ([ptkyldz](https://github.com/ptkyldz))
+ niazarak ([niazarak](https://github.com/niazarak))
+ Kevin Xu ([kevinxyc1](https://github.com/kevinxyc1))
+ Matt Yang ([justram](https://github.com/justram))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Guy Rosin ([guyrosin](https://github.com/guyrosin))
+ Eiston Wei ([eiston](https://github.com/eiston))
+ egzhbdt ([egzhbdt](https://github.com/egzhbdt))
+ Charles Wu ([charW](https://github.com/charW))
+ Matteo Catena ([catenamatteo](https://github.com/catenamatteo))
+ Andrew Yates ([andrewyates](https://github.com/andrewyates))
+ Alireza Mirzaeiyan ([amirzaeiyan](https://github.com/amirzaeiyan))
+ Antonio Mallia ([amallia](https://github.com/amallia))
+ Alex Dou ([YimingDou](https://github.com/YimingDou))
+ Horatiu Lazu ([MathBunny](https://github.com/MathBunny))
+ Edward Li ([LuKuuu](https://github.com/LuKuuu))

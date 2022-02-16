# Anserini Release Notes (v0.9.1)

**Release date: May 6, 2020**

+ Integrated metadata from CSV with JSON of full-text articles in CORD-19.
+ Renamed `Covid*` to `Cord19*` to more accurately name of corpus.
+ Updated support for CORD-19, up through data drop of 2020/05/01.
+ Added manual blacklist to skip outlier articles in CORD-19.
+ Added query generator (and output queries) from University of Delaware for TREC-COVID (round 1).
+ Added instructions for generating baseline runs for TREC-COVID (round 1).
+ Added topics for TREC-COVID (round 2).
+ Added collection support for 20Newsgroups.
+ Added support for taking stopwords from an external file.
+ Added ability to compute document frequency for phrases.
+ Added support for MS MARCO documents in Elasticsearch
+ Improved support for multiple vectors with same id in nearest neighbor search.
+ Fixed bug in Solrini regression for MS MARCO document.
+ Fixed out-of-date documentation for MS MARCO regressions.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Eiston Wei ([eiston](https://github.com/eiston))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Vera Lin ([y276lin](https://github.com/y276lin))
+ niazarak ([niazarak](https://github.com/niazarak))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Wei Pang ([weipang142857](https://github.com/weipang142857))

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
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Xin Qian ([xeniaqian94](https://github.com/xeniaqian94))
+ Adam Roegiest ([aroegies](https://github.com/aroegies))
+ Weihua Li ([w329li](https://github.com/w329li))
+ Toke Eskildsen ([tokee](https://github.com/tokee))
+ Zhaohao Zeng ([matthew-z](https://github.com/matthew-z))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
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
+ Vera Lin ([y276lin](https://github.com/y276lin))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Wei Pang ([weipang142857](https://github.com/weipang142857))
+ Ruifan Yu ([tiddler](https://github.com/tiddler))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Leonid Boytsov ([searchivarius](https://github.com/searchivarius))
+ Petek Yıldız ([ptkyldz](https://github.com/ptkyldz))
+ niazarak ([niazarak](https://github.com/niazarak))
+ Kevin Xu ([kevinxyc1](https://github.com/kevinxyc1))
+ Matt Yang ([justram](https://github.com/justram))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Guy Rosin ([guyrosin](https://github.com/guyrosin))
+ Eiston Wei ([eiston](https://github.com/eiston))
+ Charles Wu ([charW](https://github.com/charW))
+ Matteo Catena ([catenamatteo](https://github.com/catenamatteo))
+ Andrew Yates ([andrewyates](https://github.com/andrewyates))
+ Alireza Mirzaeiyan ([amirzaeiyan](https://github.com/amirzaeiyan))
+ Antonio Mallia ([amallia](https://github.com/amallia))
+ Horatiu Lazu ([MathBunny](https://github.com/MathBunny))
+ Edward Li ([LuKuuu](https://github.com/LuKuuu))

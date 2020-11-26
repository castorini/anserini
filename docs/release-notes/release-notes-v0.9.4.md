# Anserini Release Notes (v0.9.4)

**Release date: June 25, 2020**

+ Added TREC-COVID (round 4) topics and queries from UDel's query generator.
+ Added TREC-COVID (round 3) qrels and other data.
+ Added implementation of relevance feedback.
+ Added script to automate downloading a particular release of CORD-19, building indexes, and verifying.
+ Added script to automate downloading a particular pre-built index of CORD-19.
+ Added bindings of `anserini-tools` submodule to `tools/`
+ Added ingestion support for WashingtonPost v3.
+ Added ingestion support for ISO 19115 records.
+ Added ingestion support and baselines for FEVER (fact checking) dataset.
+ Added ingestion support for Common Crawl WARC and WET formats, for TREC Health Misinformation Track.
+ Added ingestion support for CC-News-En corpus.
+ Refactored code paths for ingesting ClueWeb collections.
+ Exposed better hooks to load arbitrary topics from Pyserini.
+ Removed legacy `IndexUtil` class.
+ Removed unused code paths for TREC News Track background linking task.
+ Updated Solr and ElasticSearch documentation for CORD-19.
+ Moved common MS MARCO scripts to `tools/scripts/msmarco/`, updated MS MARCO documentation.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Adam Yang ([adamyy](https://github.com/adamyy))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Mustafa Abualsaud ([ammsa](https://github.com/ammsa))
+ Pepijn Boers ([PepijnBoers](https://github.com/PepijnBoers))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Alex Dou ([YimingDou](https://github.com/YimingDou))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Justin Borromeo ([justinborromeo](https://github.com/justinborromeo))
+ Kevin Martin Jose ([kevinmartinjos](https://github.com/kevinmartinjos))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Salman Tariq ([stariqmi](https://github.com/stariqmi))
+ Tiancheng Yang ([TianchengY](https://github.com/TianchengY))

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
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Xin Qian ([xeniaqian94](https://github.com/xeniaqian94))
+ Adam Roegiest ([aroegies](https://github.com/aroegies))
+ Pepijn Boers ([PepijnBoers](https://github.com/PepijnBoers))
+ Weihua Li ([w329li](https://github.com/w329li))
+ Toke Eskildsen ([tokee](https://github.com/tokee))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Zhaohao Zeng ([matthew-z](https://github.com/matthew-z))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Adam Yang ([adamyy](https://github.com/adamyy))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Xing Niu ([xingniu](https://github.com/xingniu))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Mina Farid ([minafarid](https://github.com/minafarid))
+ Mengfei Liu ([meng-f](https://github.com/meng-f))
+ Maik Fröbe ([mam10eks](https://github.com/mam10eks))
+ Adrien Grand ([jpountz](https://github.com/jpountz))
+ Gaurav Baruah ([gauravbaruah](https://github.com/gauravbaruah))
+ Edward Lu ([edwardhdlu](https://github.com/edwardhdlu))
+ Mustafa Abualsaud ([ammsa](https://github.com/ammsa))
+ Alex Dou ([YimingDou](https://github.com/YimingDou))
+ Adrien Pouyet ([Ricocotam](https://github.com/Ricocotam))
+ Vera Lin ([y276lin](https://github.com/y276lin))
+ Alvis Wong ([wongalvis14](https://github.com/wongalvis14))
+ Wei Pang ([weipang142857](https://github.com/weipang142857))
+ Ruifan Yu ([tiddler](https://github.com/tiddler))
+ Salman Tariq ([stariqmi](https://github.com/stariqmi))
+ Leonid Boytsov ([searchivarius](https://github.com/searchivarius))
+ Rohil Gupta ([rohilG](https://github.com/rohilG))
+ Richard Xu ([richard3983](https://github.com/richard3983))
+ Petek Yıldız ([ptkyldz](https://github.com/ptkyldz))
+ niazarak ([niazarak](https://github.com/niazarak))
+ Kevin Xu ([kevinxyc1](https://github.com/kevinxyc1))
+ Kevin Martin Jose ([kevinmartinjos](https://github.com/kevinmartinjos))
+ Matt Yang ([justram](https://github.com/justram))
+ Justin Borromeo ([justinborromeo](https://github.com/justinborromeo))
+ Guy Rosin ([guyrosin](https://github.com/guyrosin))
+ Eiston Wei ([eiston](https://github.com/eiston))
+ Charles Wu ([charW](https://github.com/charW))
+ Matteo Catena ([catenamatteo](https://github.com/catenamatteo))
+ Andrew Yates ([andrewyates](https://github.com/andrewyates))
+ Alireza Mirzaeiyan ([amirzaeiyan](https://github.com/amirzaeiyan))
+ Antonio Mallia ([amallia](https://github.com/amallia))
+ Tiancheng Yang ([TianchengY](https://github.com/TianchengY))
+ Horatiu Lazu ([MathBunny](https://github.com/MathBunny))
+ Edward Li ([LuKuuu](https://github.com/LuKuuu))

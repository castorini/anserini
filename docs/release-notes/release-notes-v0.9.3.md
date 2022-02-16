# Anserini Release Notes (v0.9.3)

**Release date: May 26, 2020**

+ Updated support for CORD-19, up through data drop of 2020/05/19.
+ Added TREC-COVID (round 3) topics and queries from UDel's query generator.
+ Added ELK support for CORD-19.
+ Added ability to fetch index stats in `IndexReaderUtils`.
+ Added indexing guide for 20 Newsgroups.
+ Improved repository layout with subdirectories.
+ Improved error handling when fetching non-existent doc vectors in `IndexReaderUtils`.
+ Refactored methods for fetching {query,term}-document weights in `IndexReaderUtils`.
+ Refactored `SimplerSearcher` to improve consistency in method names.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Pepijn Boers ([PepijnBoers](https://github.com/PepijnBoers))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))

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
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Xin Qian ([xeniaqian94](https://github.com/xeniaqian94))
+ Adam Roegiest ([aroegies](https://github.com/aroegies))
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
+ Pepijn Boers ([PepijnBoers](https://github.com/PepijnBoers))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Vera Lin ([y276lin](https://github.com/y276lin))
+ Alvis Wong ([wongalvis14](https://github.com/wongalvis14))
+ Wei Pang ([weipang142857](https://github.com/weipang142857))
+ Ruifan Yu ([tiddler](https://github.com/tiddler))
+ Shane Ding ([shaneding](https://github.com/shaneding))
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
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Edward Li ([LuKuuu](https://github.com/LuKuuu))

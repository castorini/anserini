# Anserini Release Notes (v0.12.0)

**Release date: April 29, 2021**

+ Added/updated/fixed regressions for MS MARCO doc ranking, TREC 2019 DL, and TREC 2020 DL.
+ Added regressions for TREC 2020 background linking.
+ Added support for C4 Corpus.
+ Added ability to index and search pre-tokenized documents.
+ Improved end-to-end test harness.
+ Cleaned up LTR code (renamed features), improved documentation.
+ Implemented `getDocumentTokens` in `IndexReaderUtils`
+ Removed code related to knowledge graphs.
+ Refactored `TopicReader`, improved building of `TOPIC_FILE_TO_TYPE` mapping. 
+ Fixed bug in parsing of multi-line TREC topics (impact on regressions for Disks 1 & 2).
+ Fixed bug where `DocumentCollection` was not following symlinks properly.
 
## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Arthur Chen ([ArthurChen189](https://github.com/ArthurChen189))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Sailesh Nankani ([saileshnankani](https://github.com/saileshnankani))
+ Stephen Green ([eelstretching](https://github.com/eelstretching))
+ Calvin Wang ([printfCalvin](https://github.com/printfCalvin))
+ Shane Ding ([shaneding](https://github.com/shaneding))

## All Contributors

All contributors with more than one commit, sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

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
+ Boris Lin ([borislin](https://github.com/borislin))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Adam Yang ([adamyy](https://github.com/adamyy))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Xin Qian ([xeniaqian94](https://github.com/xeniaqian94))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Estella Liu ([estella98](https://github.com/estella98))
+ Pepijn Boers ([PepijnBoers](https://github.com/PepijnBoers))
+ Justin Borromeo ([justinborromeo](https://github.com/justinborromeo))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Yuxin (Vicky) Zhu ([yxzhu16](https://github.com/yxzhu16))
+ Lizzy Zhang ([LizzyZhang-tutu](https://github.com/LizzyZhang-tutu))
+ Adam Roegiest ([aroegies](https://github.com/aroegies))
+ Weihua Li ([w329li](https://github.com/w329li))
+ Yue Zhang ([nsndimt](https://github.com/nsndimt))
+ Julie Tibshirani ([jtibshirani](https://github.com/jtibshirani))
+ Toke Eskildsen ([tokee](https://github.com/tokee))
+ Zhaohao Zeng ([matthew-z](https://github.com/matthew-z))
+ Xing Niu ([xingniu](https://github.com/xingniu))
+ Alex Dou ([YimingDou](https://github.com/YimingDou))
+ Adrien Grand ([jpountz](https://github.com/jpountz))
+ Mengfei Liu ([meng-f](https://github.com/meng-f))
+ Mina Farid ([minafarid](https://github.com/minafarid))
+ Adrien Pouyet ([Ricocotam](https://github.com/Ricocotam))
+ Edward Lu ([edwardhdlu](https://github.com/edwardhdlu))
+ Gaurav Baruah ([gauravbaruah](https://github.com/gauravbaruah))
+ Mustafa Abualsaud ([ammsa](https://github.com/ammsa))
+ Jiarui Zhang ([jrzhang12](https://github.com/jrzhang12))
+ Stephen Green ([eelstretching](https://github.com/eelstretching))
+ Maik Fröbe ([mam10eks](https://github.com/mam10eks))

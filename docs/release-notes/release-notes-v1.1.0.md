# Anserini Release Notes (v1.1.0)

+ **Release date:** July 1, 2025
+ **Lucene version:** Lucene 9.9.1

## Summary of Changes

+ Added ONNX implementation of SPLADE-v3.
+ Added bindings and regressions for SPLADE-v3 on BEIR.
+ Added `GenerateRerankerRequests` to generate results for reranking from any run file.
+ Moved some prebuilt indexes to Huggingface.
+ Reorganized metadata for prebuilt indexes.
+ Reorganized `msmarco-v2.1-doc` and `msmarco-v2.1-doc-segmented` regressions for MS MARCO V2.1.
+ Updated djl and tongfei progressbar artifacts.
+ Removed REST API (Spring Boot). 

## Contributors (This Release)

Sorted by number of commits:

+ Lily Ge ([lilyjge](https://github.com/lilyjge))
+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Daniel Guo ([clides](https://github.com/clides))
+ m0v3np1ck ([ricky42613](https://github.com/ricky42613))
+ Alireza Mohaghegh Dolatabadi ([Armd04](https://github.com/Armd04))
+ Annie Zhang ([AnnieZhang2](https://github.com/AnnieZhang2))
+ Anthony Zheng ([AnthonyZ0425](https://github.com/AnthonyZ0425))
+ Carlos Lassance ([carlos-lassance](https://github.com/carlos-lassance))
+ Cassidy-Li ([Cassidy-Li](https://github.com/Cassidy-Li))
+ Chenyu Zhang ([goodzcyabc](https://github.com/goodzcyabc))
+ erfan-yazdanparast ([erfan-yazdanparast](https://github.com/erfan-yazdanparast))
+ James Begin ([James-Begin](https://github.com/James-Begin))
+ Karush Suri ([karush17](https://github.com/karush17))
+ Kevin Zhu ([kevin-zkc](https://github.com/kevin-zkc))
+ Leo Guan ([lzguan](https://github.com/lzguan))
+ Luis Felipe A. Venezian ([luisvenezian](https://github.com/luisvenezian))
+ MINGYI SU ([MINGYISU](https://github.com/MINGYISU))
+ nahalhz ([nahalhz](https://github.com/nahalhz))
+ Roselynzzz ([Roselynzzz](https://github.com/Roselynzzz))
+ sad lulu ([sadlulu](https://github.com/sadlulu))
+ sisixili ([sisixili](https://github.com/sisixili))
+ Steven Chen ([wu-ming233](https://github.com/wu-ming233))
+ Vikram Chandramohan ([Vik7am10](https://github.com/Vik7am10))
+ Vincent ([vincent-4](https://github.com/vincent-4))
+ yhzou ([Yaohui2019](https://github.com/Yaohui2019))
+ YousefNafea ([YousefNafea](https://github.com/YousefNafea))
+ Yuvaansh Kapila ([YuvaanshKapila](https://github.com/YuvaanshKapila))

## All Contributors

All contributors with five or more commits, sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Ogundepo Odunayo ([ToluClassics](https://github.com/ToluClassics))
+ Arthur Chen ([ArthurChen189](https://github.com/ArthurChen189))
+ Ahmet Arslan ([iorixxx](https://github.com/iorixxx))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Lily Ge ([lilyjge](https://github.com/lilyjge))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Jheng-Hong Yang ([justram](https://github.com/justram))
+ Royal Sequiera ([rosequ](https://github.com/rosequ))
+ Emily Wang ([emmileaf](https://github.com/emmileaf))
+ Eric Zhang ([16BitNarwhal](https://github.com/16BitNarwhal))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Vincent ([vincent-4](https://github.com/vincent-4))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Jasper Xian ([jasper-xian](https://github.com/jasper-xian))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Xinyu (Crystina) Zhang ([crystina-z](https://github.com/crystina-z))
+ Adam Yang ([adamyy](https://github.com/adamyy))
+ Mofe Adeyemi ([Mofetoluwa](https://github.com/Mofetoluwa))
+ Manveer Tamber ([manveertamber](https://github.com/manveertamber))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Matt Yang ([matthewyryang](https://github.com/matthewyryang))
+ Akintunde Oladipo ([theyorubayesian](https://github.com/theyorubayesian))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Shivani Upadhyay ([UShivani3](https://github.com/UShivani3))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Aileen Lin ([AileenLin](https://github.com/AileenLin))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Brayden Zhong ([b8zhong](https://github.com/b8zhong))
+ Nandan Thakur ([thakur-nandan](https://github.com/thakur-nandan))
+ Yuqing Xie ([amyxie361](https://github.com/amyxie361))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Steven Chen ([wu-ming233](https://github.com/wu-ming233))
+ Daniel Guo ([clides](https://github.com/clides))
+ Ryan Clancy ([ryan-clancy](https://github.com/ryan-clancy))
+ Vivek Alamuri ([valamuri2020](https://github.com/valamuri2020))

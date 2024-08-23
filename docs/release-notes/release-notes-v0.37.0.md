# Anserini Release Notes (v0.37.0)

+ **Release date:** August 22, 2024
+ **Lucene version:** Lucene 9.9.1

## Summary of Changes

+ Added support for indexing and searching flat (dense) vectors.
+ Added prebuilt flat indexes and repro bindings for the BGE embedding model.
+ Added bindings for researchy questions and the TREC 2024 RAG Track test set.
+ Added new regressions with prebuilt indexes.
+ Improved metadata for prebuilt indexes.
+ Improved documentation for ONNX models.
+ Improved webapp and REST API.
  + Created new versioned routes.
  + Refined UI components.
+ Upgraded `ai.djl` and fixed token length issue.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Eric Zhang ([16BitNarwhal](https://github.com/16BitNarwhal))
+ Andre Slavescu ([AndreSlavescu](https://github.com/AndreSlavescu))
+ Vivek Alamuri ([valamuri2020](https://github.com/valamuri2020))
+ Alireza Taban ([alireza-taban](https://github.com/alireza-taban))
+ Chun-Wei ([bilet-13](https://github.com/bilet-13))
+ Daisy Ye ([daisyyedda](https://github.com/daisyyedda))
+ Emily Yu ([emily-emily](https://github.com/emily-emily))
+ Eric Wang ([IR3KT4FUNZ](https://github.com/IR3KT4FUNZ))
+ Faizan Faisal ([FaizanFaisal25](https://github.com/FaizanFaisal25))
+ Hosna Oyarhoseini ([hosnahoseini](https://github.com/hosnahoseini))
+ MariaPonomarenko38 ([MariaPonomarenko38](https://github.com/MariaPonomarenko38))
+ Mehrnaz Sadeghieh ([MehrnazSadeghieh](https://github.com/MehrnazSadeghieh))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Xiaoyan Song ([SeanSong25](https://github.com/SeanSong25))
+ Yidi Chen ([XKTZ](https://github.com/XKTZ))
+ Yiran Sun ([Feng-12138](https://github.com/Feng-12138))
+ Alireza Nasirian ([alireza-nasirian](https://github.com/alireza-nasirian))
+ Nathan Kuissi ([natek-1](https://github.com/natek-1))
+ npjd ([npjd](https://github.com/npjd))

## All Contributors

All contributors with five or more commits, sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Ogundepo Odunayo ([ToluClassics](https://github.com/ToluClassics))
+ Arthur Chen ([ArthurChen189](https://github.com/ArthurChen189))
+ Ahmet Arslan ([iorixxx](https://github.com/iorixxx))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Emily Wang ([emmileaf](https://github.com/emmileaf))
+ Royal Sequiera ([rosequ](https://github.com/rosequ))
+ Jheng-Hong Yang ([justram](https://github.com/justram))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Eric Zhang ([16BitNarwhal](https://github.com/16BitNarwhal))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Jasper Xian ([jasper-xian](https://github.com/jasper-xian))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Mofe Adeyemi ([Mofetoluwa](https://github.com/Mofetoluwa))
+ Xinyu (Crystina) Zhang ([crystina-z](https://github.com/crystina-z))
+ Adam Yang ([adamyy](https://github.com/adamyy))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Manveer Tamber ([manveertamber](https://github.com/manveertamber))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Akintunde Oladipo ([theyorubayesian](https://github.com/theyorubayesian))
+ Matt Yang ([d1shs0ap](https://github.com/d1shs0ap))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Aileen Lin ([AileenLin](https://github.com/AileenLin))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Nandan Thakur ([thakur-nandan](https://github.com/thakur-nandan))
+ Yuqing Xie ([amyxie361](https://github.com/amyxie361))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Ryan Clancy ([ryan-clancy](https://github.com/ryan-clancy))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))

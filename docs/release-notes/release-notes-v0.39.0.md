# Anserini Release Notes (v0.39.0)

+ **Release date:** January 12, 2025
+ **Lucene version:** Lucene 9.9.1

## Summary of Changes

+ Added support for indexing dense vectors in Parquet format:
  + Created `ParquetDenseVectorCollection` and `ParquetDenseVectorDocumentGenerator`.
  + Used `strategicblue/parquet-floor` dependency.
  + Refactored `DenseVectorDocumentGenerator` to `JsonDenseVectorDocumentGenerator`.
  + Refactored `InvertedDenseVectorDocumentGenerator` to `JsonInvertedDenseVectorDocumentGenerator`.
  + Established parallel class structure for jsonl and Parquet formats.
  + Added dynamic type support (float/double) for `ParquetDenseVectorDocumentGenerator`.
+ Added bindings: RAG24 topics with Arctic emeddings, RAG24 UMBRELA qrels.
+ Added initial fusion implementation: regressions script w/ initial yaml config.
+ Installed new regressions: RAG24 baselines, MS MARCO Passage V1 Parquet, BEIR Parquet.
+ Refactored approach to tolerance checking and tweaked yaml configs.
+ Improved search UI landing page and search bar when displaying results.
+ Renamed `cos-dpr` to `cos-dpr`: refactored prebuilt indexes, symbols etc.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Shivani Upadhyay ([UShivani3](https://github.com/UShivani3))
+ Jie Min ([Stefan824](https://github.com/Stefan824))
+ Vivek Alamuri ([valamuri2020](https://github.com/valamuri2020))
+ Brayden Zhong ([b8zhong](https://github.com/b8zhong))
+ Vincent ([vincent-4](https://github.com/vincent-4))
+ Daniel Zhang ([zdann15](https://github.com/zdann15))
+ Ahmed Essam ([AhmedEssam19](https://github.com/AhmedEssam19))
+ Alireza Arbabi ([Alireza-Zwolf](https://github.com/Alireza-Zwolf))
+ Amirkia RAFIEI OSKOOEI ([amirkiarafiei](https://github.com/amirkiarafiei))
+ Anshul Singh ([anshulsc](https://github.com/anshulsc))
+ Carol Duan ([CCarolD](https://github.com/CCarolD))
+ Divyajyoti Panda ([Divyajyoti02](https://github.com/Divyajyoti02))
+ Forrest Gao ([Linsen-gao-457](https://github.com/Linsen-gao-457))
+ Hossein Molaeian ([Hossein-Molaeian](https://github.com/Hossein-Molaeian))
+ Jialin ([sherloc512](https://github.com/sherloc512))
+ Katelyn Harlan ([Axiomatic314](https://github.com/Axiomatic314))
+ Krish Patel ([krishh-p](https://github.com/krishh-p))
+ Nicole Han ([nicoella](https://github.com/nicoella))
+ Nihal Menon ([nihalmenon](https://github.com/nihalmenon))
+ Patrick Yi ([pjyi2147](https://github.com/pjyi2147))
+ Raghav Vasudeva ([Raghav0005](https://github.com/Raghav0005))
+ Raya Ferdous ([r-aya](https://github.com/r-aya))
+ Rohan Jha ([robro612](https://github.com/robro612))
+ Samantha ([Samantha-Zhan](https://github.com/Samantha-Zhan))
+ Shreyas Patil ([ShreyasP20](https://github.com/ShreyasP20))
+ a-y-m-a-n-c-h ([a-y-m-a-n-c-h](https://github.com/a-y-m-a-n-c-h))
+ andrewxucs ([andrewxucs](https://github.com/andrewxucs))
+ mithildamani256 ([mithildamani256](https://github.com/mithildamani256))
+ nourj98 ([nourj98](https://github.com/nourj98))
+ pxlin-09 ([pxlin-09](https://github.com/pxlin-09))
+ sisixili ([sisixili](https://github.com/sisixili))

## All Contributors

All contributors with five or more commits, sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Ogundepo Odunayo ([ToluClassics](https://github.com/ToluClassics))
+ Arthur Chen ([ArthurChen189](https://github.com/ArthurChen189))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Ahmet Arslan ([iorixxx](https://github.com/iorixxx))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Emily Wang ([emmileaf](https://github.com/emmileaf))
+ Royal Sequiera ([rosequ](https://github.com/rosequ))
+ Jheng-Hong Yang ([justram](https://github.com/justram))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Eric Zhang ([16BitNarwhal](https://github.com/16BitNarwhal))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Jasper Xian ([jasper-xian](https://github.com/jasper-xian))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Xinyu (Crystina) Zhang ([crystina-z](https://github.com/crystina-z))
+ Adam Yang ([adamyy](https://github.com/adamyy))
+ Mofe Adeyemi ([Mofetoluwa](https://github.com/Mofetoluwa))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Manveer Tamber ([manveertamber](https://github.com/manveertamber))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Akintunde Oladipo ([theyorubayesian](https://github.com/theyorubayesian))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Matt Yang ([d1shs0ap](https://github.com/d1shs0ap))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Aileen Lin ([AileenLin](https://github.com/AileenLin))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Vivek Alamuri ([valamuri2020](https://github.com/valamuri2020))
+ Ryan Clancy ([ryan-clancy](https://github.com/ryan-clancy))
+ Yuqing Xie ([amyxie361](https://github.com/amyxie361))
+ Nandan Thakur ([thakur-nandan](https://github.com/thakur-nandan))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Shivani Upadhyay ([UShivani3](https://github.com/UShivani3))

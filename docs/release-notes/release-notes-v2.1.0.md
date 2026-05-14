# Anserini Release Notes (v2.1.0)

+ **Release date:** May 14, 2026
+ **Lucene version:** Lucene 10.4.0

## Summary of Changes

+ Upgraded BEIR to prebuilt BGE flat vector indexes with Lucene 10.4.0.
+ Reorganized documentation to increase legibility to agents.
+ Reorganized resolution of cache directory.
+ Reorganized and improved agent skills
+ Refactored and cleaned up tests.
+ Added `GetDocument` CLI.
+ Added `EncodeQuery` CLI to encode queries.
+ Added REST index alias configuration support.
+ Added support for NanoKnow.
+ Added support for RAG 25.
+ Renamed catalog CLIs to registries.
+ Reduced fatjar size by excluding native debug symbols.
+ Removed obsolete regression scripts.

## Known Issues

+ [`anserini-tools#109`](https://github.com/castorini/anserini-tools/pull/109) recreated BGE cached queries for BEIR. This results in minor differences with reproductions from raw document collections with the cached queries.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Lingwei Gu ([lingwei-gu](https://github.com/lingwei-gu))
+ Shivani Upadhyay ([UShivani3](https://github.com/UShivani3))
+ Alex Wang ([alex-wang101](https://github.com/alex-wang101))
+ DHRUV DUBEY ([zatchbell1311-wq](https://github.com/zatchbell1311-wq))
+ h79yan ([h79yan](https://github.com/h79yan))
+ kwamearhinPORTFL ([kwamearhinPORTFL](https://github.com/kwamearhinPORTFL))
+ Mazharul Islam Leon ([mazleon](https://github.com/mazleon))
+ mohamedshakir3 ([mohamedshakir3](https://github.com/mohamedshakir3))
+ Neng Li ([nli33](https://github.com/nli33))
+ Oluwaseun Ajayi ([Seun-Ajayi](https://github.com/Seun-Ajayi))
+ Tahseen Rasheed Chowdhury ([TahseenSust](https://github.com/TahseenSust))
+ Uchenna Uchechukwu-Njoku ([blissuche90](https://github.com/blissuche90))
+ Xianda Du ([XiandaDu](https://github.com/XiandaDu))
+ Zixi Tang ([Zixi-Sam-Tang](https://github.com/Zixi-Sam-Tang))

## All Contributors

All contributors with five or more commits, sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors?all=1):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Lily Ge ([lilyjge](https://github.com/lilyjge))
+ Ogundepo Odunayo ([ToluClassics](https://github.com/ToluClassics))
+ Arthur Chen ([ArthurChen189](https://github.com/ArthurChen189))
+ Ahmet Arslan ([iorixxx](https://github.com/iorixxx))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Jheng-Hong Yang ([justram](https://github.com/justram))
+ Royal Sequiera ([rosequ](https://github.com/rosequ))
+ Emily Wang ([emmileaf](https://github.com/emmileaf))
+ Eric Zhang ([16BitNarwhal](https://github.com/16BitNarwhal))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Vincent ([vz-gh](https://github.com/vz-gh))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Shivani Upadhyay ([UShivani3](https://github.com/UShivani3))
+ Jasper Xian ([jasper-xian](https://github.com/jasper-xian))
+ Daniel Guo ([clides](https://github.com/clides))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Adam Yang ([adamyy](https://github.com/adamyy))
+ Mofe Adeyemi ([Mofetoluwa](https://github.com/Mofetoluwa))
+ Xinyu (Crystina) Zhang ([crystina-z](https://github.com/crystina-z))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Manveer Tamber ([manveertamber](https://github.com/manveertamber))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Matt Yang ([matthewyryang](https://github.com/matthewyryang))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Lingwei Gu ([lingwei-gu](https://github.com/lingwei-gu))
+ FarmersWrap ([FarmersWrap](https://github.com/FarmersWrap))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Akintunde Oladipo ([theyorubayesian](https://github.com/theyorubayesian))
+ Brayden Zhong ([b8zhong](https://github.com/b8zhong))
+ Suraj Subrahmanyan ([suraj-subrahmanyan](https://github.com/suraj-subrahmanyan))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ Sahel Sharifymoghaddam ([sahel-sh](https://github.com/sahel-sh))
+ Aileen Lin ([AileenLin](https://github.com/AileenLin))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ JJGreen0 ([JJGreen0](https://github.com/JJGreen0))
+ Yuqing Xie ([amyxie361](https://github.com/amyxie361))
+ Nandan Thakur ([thakur-nandan](https://github.com/thakur-nandan))
+ Steven Chen ([wu-ming233](https://github.com/wu-ming233))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Ryan Clancy ([ryan-clancy](https://github.com/ryan-clancy))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Vivek Alamuri ([valamuri2020](https://github.com/valamuri2020))

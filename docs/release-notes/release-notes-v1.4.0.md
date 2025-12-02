# Anserini Release Notes (v1.4.0)

+ **Release date:** December 2, 2025
+ **Lucene version:** Lucene 9.9.1

## Summary of Changes

+ Refactored entire codebase to get rid of all compiler warnings.
+ Refactored unit tests.
+ Refactored `PrebuiltIndexHandler`.
+ Refactored to eliminate `fastutil` dependency.
+ Fixed ONNX encoder truncation bug when processing long queries (affects Arguna results in BEIR).
+ Updated dependencies to latest versions (with some exceptions).
+ Added symbols for MS MARCO prebuilt indexes.

## Contributors (This Release)

Sorted by number of commits:

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ FarmersWrap ([FarmersWrap](https://github.com/FarmersWrap))
+ Suraj Subrahmanyan ([suraj-subrahmanyan](https://github.com/suraj-subrahmanyan))
+ Lily Ge ([lilyjge](https://github.com/lilyjge))
+ Minjee Yang ([minj22](https://github.com/minj22))
+ Adrian Gri ([AdrianGri](https://github.com/AdrianGri))
+ amir_hosseinpoor ([Amirhosseinpoor](https://github.com/Amirhosseinpoor))
+ Aniruddh Thakur ([AniruddhThakur](https://github.com/AniruddhThakur))
+ Blank9999 ([Blank9999](https://github.com/Blank9999))
+ Brandon Zhou ([brandonzhou2002](https://github.com/brandonzhou2002))
+ Henry ([henry4516](https://github.com/henry4516))
+ Inan Syed ([InanSyed](https://github.com/InanSyed))
+ Ivan ([ivan-0862](https://github.com/ivan-0862))
+ jianxyou ([jianxyou](https://github.com/jianxyou))
+ JJGreen0 ([JJGreen0](https://github.com/JJGreen0))
+ Kevin Wang ([k464wang](https://github.com/k464wang))
+ Lihua ([LiHuua258](https://github.com/LiHuua258))
+ Lucas Kim ([Raptors65](https://github.com/Raptors65))
+ Mahdi Behnam ([mahdi-behnam](https://github.com/mahdi-behnam))
+ Mahdi Noori ([MahdiNoori2003](https://github.com/MahdiNoori2003))
+ Pouya Sadeghi ([Ipouyall](https://github.com/Ipouyall))
+ Praveen ([prav0761](https://github.com/prav0761))
+ rashadjn ([rashadjn](https://github.com/rashadjn))
+ Richmond Boahene ([RichHene](https://github.com/RichHene))
+ Ruiyang Zhang ([royary](https://github.com/royary))
+ Sahel Sharifymoghaddam ([sahel-sh](https://github.com/sahel-sh))
+ samin ([samin-mehdizadeh](https://github.com/samin-mehdizadeh))
+ Shreya Adrita Banik ([shreyaadritabanik](https://github.com/shreyaadritabanik))
+ Tam Nguyen ([ball2004244](https://github.com/ball2004244))
+ Xincan Feng ([xincanfeng](https://github.com/xincanfeng))
+ Yazdan ZandiyeVakili ([yazdanzv](https://github.com/yazdanzv))

## All Contributors

All contributors with five or more commits, sorted by number of commits, [according to GitHub](https://github.com/castorini/Anserini/graphs/contributors):

+ Jimmy Lin ([lintool](https://github.com/lintool))
+ Peilin Yang ([Peilin-Yang](https://github.com/Peilin-Yang))
+ Lily Ge ([lilyjge](https://github.com/lilyjge))
+ Ogundepo Odunayo ([ToluClassics](https://github.com/ToluClassics))
+ Arthur Chen ([ArthurChen189](https://github.com/ArthurChen189))
+ Xueguang Ma ([MXueguang](https://github.com/MXueguang))
+ Ahmet Arslan ([iorixxx](https://github.com/iorixxx))
+ Tommaso Teofili ([tteofili](https://github.com/tteofili))
+ Edwin Zhang ([edwinzhng](https://github.com/edwinzhng))
+ Rodrigo Nogueira ([rodrigonogueira4](https://github.com/rodrigonogueira4))
+ Jheng-Hong Yang ([justram](https://github.com/justram))
+ Emily Wang ([emmileaf](https://github.com/emmileaf))
+ Royal Sequiera ([rosequ](https://github.com/rosequ))
+ Eric Zhang ([16BitNarwhal](https://github.com/16BitNarwhal))
+ Yuqi Liu ([yuki617](https://github.com/yuki617))
+ Vincent ([vincent-4](https://github.com/vincent-4))
+ Victor Yang ([Victor0118](https://github.com/Victor0118))
+ Chris Kamphuis ([Chriskamphuis](https://github.com/Chriskamphuis))
+ Boris Lin ([borislin](https://github.com/borislin))
+ Nikhil Gupta ([nikhilro](https://github.com/nikhilro))
+ Jasper Xian ([jasper-xian](https://github.com/jasper-xian))
+ Ronak Pradeep ([ronakice](https://github.com/ronakice))
+ Yuhao Xie ([Kytabyte](https://github.com/Kytabyte))
+ Shane Ding ([shaneding](https://github.com/shaneding))
+ Daniel Guo ([clides](https://github.com/clides))
+ Stephanie Hu ([stephaniewhoo](https://github.com/stephaniewhoo))
+ Kuang Lu ([lukuang](https://github.com/lukuang))
+ Xinyu (Crystina) Zhang ([crystina-z](https://github.com/crystina-z))
+ Mofe Adeyemi ([Mofetoluwa](https://github.com/Mofetoluwa))
+ Joel Mackenzie ([JMMackenzie](https://github.com/JMMackenzie))
+ Adam Yang ([adamyy](https://github.com/adamyy))
+ Salman Mohammed ([salman1993](https://github.com/salman1993))
+ Xinyu Mavis Liu ([x389liu](https://github.com/x389liu))
+ Manveer Tamber ([manveertamber](https://github.com/manveertamber))
+ Shivani Upadhyay ([UShivani3](https://github.com/UShivani3))
+ Luchen Tan ([LuchenTan](https://github.com/LuchenTan))
+ Matt Yang ([matthewyryang](https://github.com/matthewyryang))
+ Kelvin Jiang ([kelvin-jiang](https://github.com/kelvin-jiang))
+ Johnson Han ([x65han](https://github.com/x65han))
+ Hang Cui ([HangCui0510](https://github.com/HangCui0510))
+ Akintunde Oladipo ([theyorubayesian](https://github.com/theyorubayesian))
+ Zhiying Jiang ([bazingagin](https://github.com/bazingagin))
+ Aileen Lin ([AileenLin](https://github.com/AileenLin))
+ Brayden Zhong ([b8zhong](https://github.com/b8zhong))
+ Michael Tu ([tuzhucheng](https://github.com/tuzhucheng))
+ Suraj Subrahmanyan ([suraj-subrahmanyan](https://github.com/suraj-subrahmanyan))
+ Dayang Shi ([dyshi](https://github.com/dyshi))
+ JJGreen0 ([JJGreen0](https://github.com/JJGreen0))
+ Peng Shi ([Impavidity](https://github.com/Impavidity))
+ Zeynep Akkalyoncu Yilmaz ([zeynepakkalyoncu](https://github.com/zeynepakkalyoncu))
+ Vivek Alamuri ([valamuri2020](https://github.com/valamuri2020))
+ Nandan Thakur ([thakur-nandan](https://github.com/thakur-nandan))
+ Steven Chen ([wu-ming233](https://github.com/wu-ming233))
+ Ryan Clancy ([ryan-clancy](https://github.com/ryan-clancy))
+ Yuqing Xie ([amyxie361](https://github.com/amyxie361))

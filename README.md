Anserini
========

Build using Maven:

```
mvn clean package appassembler:assemble
```

* Tools

    * [IndexCollection](docs/index-collection.md)
    * [DumpIndex](docs/dumpindex.md)
        * [DumpIndex Reference](docs/dumpindex-reference.md)
    * [SearchCollection](docs/search-collection.md)

* Anserini is designed to support experiments on various standard TREC collections out of the box:

    * [Disk12](docs/experiments-disk12.md)
    * [Robust04](docs/experiments-robust04.md)
    * [WT2G & WT10G](docs/experiments-wt.md)
    * [Gov2](docs/experiments-gov2.md)
    * [ClueWeb09b](docs/experiments-clueweb09b.md)
    * [ClueWeb12-B13](docs/experiments-clueweb12-b13.md)

* Other features

    * [Twitter (Near) Real-Time Search](docs/twitter-nrts.md)
    * [YoGosling](docs/yogosling.md)
    
* How-To

    * [Add a new collection class](docs/add-collection-class.md)
    * [Add a new topic reader](docs/add-topic-reader.md)

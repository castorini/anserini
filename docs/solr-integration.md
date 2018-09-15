# Solr Integration

Anserini, using `IndexCollection`, generates Lucene index files that we can load into [Solr](http://lucene.apache.org/solr/). Solr is a search engine build on Lucene that has desirable tools such as an interface to perform queries on Lucene (Anserini) indices.

Docker
======

In order to integrate Anserini and Solr, we'll be using [Docker](https://www.docker.com/) - make sure this is setup on your machine before continuing.

Overview
========

Loading a Lucene index into Solr is fairly straightforward as Solr is built on top of Lucene. In a nutshell, the following needs to happen:

1. Create the Solr core (index) that will hold our data.
2. Copy the Lucene index files into the `<my_core>/data/index/` directory of the Solr server.
3. Update the schema (`<my_core>/conf/managed-schema`) file to match the fields in our index.
4. Reload the core.

This has been automated through a number of scripts to automatically load `core17` and `mb11` collection indices into Solr.

Instructions
============

1. Build the Docker image for anserini-solr
    - `docker build -t anserini-solr .`
2. Edit the `.docker/run.sh` file to point at the directory where your Anserini generated Lucene indices are.
3. Execute the `.docker/run.sh` file.
4. Execute the `load.sh` script within the Docker container.
    - `docker exec solr ./load.sh`
5. Reload each core from the admin UI (`http://localhost:8983`).
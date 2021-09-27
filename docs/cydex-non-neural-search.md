# COVID-19 Open Research Dataset Search

This page contains instructions to create a [Cydex Instance](http://cydex.ai) for searching scholarly articles from a collection of bibtex records without neural re-ranking.

## Indexing 

- First, we need to index the Bibtex collection using [Anserini](https://github.com/castorini/anserini). [Follow these instructions](https://github.com/castorini/anserini#getting-started) to clone and build Anserini.


- After cloning, We can now index the bibtex docs as a `BibtexCollection` using Anserini:

    ```bash
    sh target/appassembler/bin/IndexCollection \
    -collection BibtexCollection -generator BibtexGenerator \
    -threads 8 -input {/path/to/bib_files/} \
    -index {/path/to/bibtex_indexes} \
    -storePositions -storeDocvectors -storeContents -storeRaw
    ```

  The directory `/path/to/bib_files/` should be a directory containing `.bib` files that will be used for search and retrieval
  
  For additional details, see explanation of [common indexing options](common-indexing-options.md).


- Clone the [Covidex](https://github.com/castorini/covidex) repository
  ```
  git clone https://github.com/castorini/covidex.git
  ``` 

- Copy the indexed files from `/path/to/bibtex_indexes` folder into `api/index` folder in the covidex repository

## Local Deployment

#### API Server


- Install [Anaconda](https://docs.anaconda.com/anaconda/install) (currently version 2020.11)

- Set up environment variables by copying over the defaults and modifying as needed
    ```
    cp api/.env.sample api/.env
    ```
  - Open the `.env` file and change the `T5_DEVICE` environment variable from cuda to cpu since there is no neural re-ranking involved.
  - Also change the `INDEX_PATH` environment variable to the file path containing the index files

- Create an Anaconda environment for Python 3.7
    ```
    conda create -n covidex python=3.7
    ```

- Activate the Anaconda environment
    ```
    conda activate covidex
    ```

- Install Python dependencies
    ```
    pip install -r api/requirements.txt
    ```

- Run the server (make sure you are in the `api/` folder)
    ```
    uvicorn app.main:app --reload --port=8000
    ```

The server wil be running at [localhost:8000](http://localhost:8000) with API documentation at [/docs](http://localhost:8000/docs)


#### UI Client

- Install  [Node.js 12+](https://nodejs.org/en/download/) and [Yarn](https://classic.yarnpkg.com/en/docs/install/).

- Install dependencies
    ```
    yarn install
    ```

- Start the server
    ```
    yarn start
    ```

The client will be running at [localhost:3000](http://localhost:3000)




## Testing

To run all API tests
```
TESTING=true pytest api
```

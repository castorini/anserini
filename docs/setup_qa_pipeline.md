# Steps to setup an end2end QA pipeline 
Clone the Anserini, models, and data repo:
```
git clone https://github.com/castorini/Anserini.git
git clone https://github.com/castorini/data.git
git clone https://github.com/castorini/models.git
```


You should have the following directory structure after cloning:
```
.
├── Anserini
├── data
└── models

```

Build Anserini:
```
cd Anserini
mvn clean package appassembler:assemble
```

We highly recommend the use of [virtualenv](https://virtualenv.pypa.io/en/stable/) as the dependencies 
are subjected to frequent changes.

Install the dependency packages:

```
pip3 install -r requirements.txt 
```

Make sure that you have PyTorch installed. For more help, follow [these](https://github.com/castorini/Castor) steps.

### Additional files for pipeline:
As some of the files are too large to be uploaded onto GitHub, please download the following files from
 [here](https://drive.google.com/drive/folders/0B2u_nClt6NbzNm1LdjlwUFdzQVE?usp=sharing) and place them
in the appropriate locations:

 - copy the contents of `word2vec` directory to `data/word2vec`
 - copy `word2dfs.p` to `data/TrecQA/`

### Starting up the end2end system:

Make sure that you have correctly set your `config` file. For help, refer 
[this](https://github.com/castorini/Anserini/blob/master/docs/speech-ui-api-docs.md).

Start the Gateway server, Flask API, and the javascript app:

```
chmod +x run_ui.sh
./run_ui.sh
```


__NB:__  The speech UI cannot be run in Ubuntu. To test the pipeline in Ubuntu, make the following changes: 
- Comment out the JavaScript part and run the Bash script
- Make a REST API query to the endpoint using Postman, Curl etc.
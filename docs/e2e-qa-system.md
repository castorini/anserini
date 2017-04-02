# Get all the repos in castorini and arrange them in this format
- castorini
    -- Anserini
    -- Castor
    -- data
    -- models
    
# Configure the speech-UI in Anserini
- make the config file and add in the secret key for Wit API
​
# Getting the S&M model to src/main/python
- Copy the directory Castor/sm_model to Anserini/src/main/python/sm_model
- Copy the file Castor/castorini_smmodel_bridge.py to Anserini/src/main/python/
​
# Files for the model, w2v, word2dfs, stopwords
- Train the model or use the pre-trained one in models/sm_model/sm_model.TrecQA.TRAIN-ALL.2017-04-02.castor
- Training the model also produces word2vec file that is also needed later
- Build the TrecQA Dataset - https://github.com/castorini/data/tree/master/TrecQA
- This produces the word2dfs.p file that is also needed later
- The stopwords.txt file is located under data/TrecQA/
​
# Making changes in Flask api
- Open up the file Anserini/src/main/python/api.py and make these changes:
```
from castorini_smmodel_bridge import SMModelBridge
model = SMModelBridge(...[change the paths here for the model, w2v, stopwords, word2dfs.p]....)
```
- Then change the get_answers(..) method
```
def get_answers(question, num_hits, k):
    pyserini = Pyserini(app.config.get('index'))
    candidate_passages_scores = pyserini.ranked_passages(question, num_hits, k)
    candidate_passages = []
    for ps in candidate_passages_scores:
        ps_split = ps.split('\t')
        candidate_passages.append(ps_split[0])
​
    answers_list = model.rerank_candidate_answers(question, candidate_passages)
    answers = []
    for score, sent in answers_list:
        answers.append({'passage': sent, 'score': score})
​
    return answers
```
​
# Starting up the Flask server and test
- start the Gateway server, Flask API, and the javascript app
- Modify permissions and run the Bash script: ./run_ui.sh 
- Test out the app:
    -- In Mac, you will see the menubar icon
    -- In Ubuntu, make a REST API query to the endpoint using Postman
    -- For Ubuntu, you can comment out the JavaScript part and run the Bash script
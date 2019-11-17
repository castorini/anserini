import json
import os,sys
import subprocess


'''
this moduele can run indexing and searching on given data collection(eg. robust04,WashingPost)

It must run under parental directory "anserini" with supported solr path

It required 1 parameter(collectin name) as input to indentify all other collection information in
json format "index_options" below.

This instruction can help you to understand how it works:
https://github.com/castorini/anserini/blob/master/docs/solrini.md

'''

# Options for the different supported collections.
index_options = {
    "core18": {
        "collection": "WashingtonPostCollection",
        "generator": "WapoGenerator", 
        "input_path":"core18/WashingtonPost.v2/data", ### Modify it based on ur own collection path
        "thread_num": 8,
        "topic_reader":"Trec",
        "topic_path":"src/main/resources/topics-and-qrels/topics.core18.txt",
        "qrel_path":"src/main/resources/topics-and-qrels/qrels.core18.txt"
    },
    "core17": {
        "collection": "NewYorkTimesCollection",
        "generator": "JsoupGenerator"
    },
    "cw09b": {
        "collection": "ClueWeb09Collection",
        "generator": "JsoupGenerator"
    },
    "cw12b": {
        "collection": "ClueWeb12Collection",
        "generator": "JsoupGenerator"
    },
    "gov2": {
        "collection": "TrecwebCollection",
        "generator": "JsoupGenerator"
    },
    "robust04": {
        "collection": "TrecCollection",
        "generator": "JsoupGenerator", 
        "input_path":"myrobust04/disk45", ### Modify it based on ur own collection path
        "thread_num": 8,
        "topic_reader":"Trec",
        "topic_path":"src/main/resources/topics-and-qrels/topics.robust04.txt",
        "qrel_path":"src/main/resources/topics-and-qrels/qrels.robust04.txt"
    }
}



class Solr_command:
    def __init__(self,arguments):
        self.solr_on = "solrini/bin/solr start -c -m 8G"
        self.solr_config = "pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd"
        self.name = arguments[0]

    def command_execution(self, command):
        print(command)
        subprocess.run(command, shell = True)

    
    def turn_on_server(self):    
        print("we turn on solr server")
        self.command_execution(self.solr_on)
        self.command_execution(self.solr_config)  
        print("server is on")


    def turn_off_server(self):
        subprocess.run("solrini/bin/solr stop -all",shell=True)        

    ### we consider 9983 as default local port number
    ### ignore it if you receive message: Collection 'xxx' already exist. It just means you previouly build 
    ### this collection on solr already, re-build does not affect anything 
    def indexing(self,name,input_path,thread_num):
        thread_num = str(thread_num)
        print("Create data collection on Solr")
        command1 = "solrini/bin/solr create -n anserini -c {}".format(name)     
        self.command_execution(command1)

        collection, generator = index_options[name]["collection"],index_options[name]["generator"]
        print("\nsolr indexing start for {}, {}, {}, {}, {} \n".format(collection,generator,name,input_path,thread_num))    
        command2 = """sh target/appassembler/bin/IndexCollection -collection {} -generator {} -threads {} -input {}  -solr -solr.index {} -solr.zkUrl localhost:9983  -storePositions -storeDocvectors -storeTransformedDocs""".format(collection,generator,thread_num,input_path,name)
        self.command_execution(command2)   
        print("\nindexing complete\n")
    
    ### we consider 8983 as default local port number
    def searching(self,name,topic_reader, topic_path):
        output_path = "run.solr.{}.bm25.topics.{}.301-450.601-700.txt".format(name,name)       
        command = "sh target/appassembler/bin/SearchSolr -topicreader {} -solr.index {} -solr.zkUrl localhost:9983 -topics {} -output run.solr.{}.bm25.topics.{}.301-450.601-700.txt""".format(topic_reader,name,topic_path,name,name) 
        self.command_execution(command)   
        print("searching complete")
        print("output file path: ", output_path)       
        return output_path


    def trec_eval(self,qrel_path, output_path):
        print("\nWe start trec_eval: \n")
        command = "eval/trec_eval.9.0.4/trec_eval -m map -m P.30 {} {}".format(qrel_path,output_path)
        self.command_execution(command)   

    #### run solr indexing,search on series of data colletion 
    def data_collection_process(self,name):
        ###It required 8 parameter: collectin_name,input_path,thread_num,topic_reader,topic_path,qrel_path
        ### collection, generator
        input_path, thread_num = index_options[name]["input_path"],index_options[name]["thread_num"]  
        collection, generator = index_options[name]["collection"],index_options[name]["generator"]
        topic_reader = index_options[name]["topic_reader"]
        topic_path,qrel_path = index_options[name]["topic_path"],index_options[name]["qrel_path"]
        print('Input: ', name, input_path, thread_num,topic_reader)
        print("topic path: ", topic_path)   
        print("qrel path: ", qrel_path)     

        self.indexing(name,input_path,thread_num)
        output_path = self.searching(name, topic_reader, topic_path)
        self.trec_eval(qrel_path, output_path)   


if __name__ == "__main__":
    solr = None
    try:
        print(sys.argv)
        solr = Solr_command(sys.argv[1:])
    except:
        sys.exit("Invalid input")

    """
    it take all these step to finish server setup,document indexing,data retrieval,evaluation with BM25,
    execute it one by one
    """
    solr.turn_on_server()  
    solr.data_collection_process(solr.name)
    ###solr.turn_off_server()  




    

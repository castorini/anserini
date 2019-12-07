import json
import os,sys
import subprocess
import argparse
import requests  


'''
This module can execute index and search on given data collection(eg. robust04,WashingPost)

It must run under parental directory "anserini" with supported solrini package 

This module requires 2 parameters (collection name and collection path) as input to extract 
 further collection information based on "index_options" below, which is in json format.

More details can be found here:


https://github.com/castorini/anserini/blob/master/docs/solrini.md

'''

# Options for the different supported collections.
index_options = {
    "core18": {
        "collection": "WashingtonPostCollection",
        "generator": "WapoGenerator", 
        ##"input_path":"core18/WashingtonPost.v2/data", ### Modify it based on ur own collection path
        "thread_num": 8,
        "topic_reader":"Trec",
        "topic_path":"src/main/resources/topics-and-qrels/topics.core18.txt",
        "qrel_path":"src/main/resources/topics-and-qrels/qrels.core18.txt",   
        "expected_indexed_doc":595037, 
        "MAP": "0.2495",
        "P30": "0.3567"   
    },
    "robust04": {
        "collection": "TrecCollection",
        "generator": "JsoupGenerator", 
        "thread_num": 8,
        "topic_reader":"Trec",
        "topic_path":"src/main/resources/topics-and-qrels/topics.robust04.txt",
        "qrel_path":"src/main/resources/topics-and-qrels/qrels.robust04.txt"
    }
}


# read_file_into_list works with trec_eval() to extract actual MAP, P_30 results from "this_is_temp_file.txt" file
def read_file_into_list(keywords):
    if (os.path.exists("this_is_temp_file.txt") == True):
        with open("this_is_temp_file.txt", "r") as f:
            content = f.read().splitlines()   
        res = []
        for keyword in keywords:
            for n in content:
                if (keyword in n):res.append(n)
    ###print(res) 
    return res


class SolrClient:
    def __init__(self,name,input_path,skip_index):
        if (name == None): raise Exception("Need collection name, but received None")
        if (input_path == None): raise Exception("Need path of collection, but received None")
        self.name = name 
        self.input_path = input_path
        self.output_path = "run.solr.{}.bm25.topics.{}.txt".format(name,name)       
        self.skip_index = skip_index   

    def command_execution(self, command_path, command):
        print(command)
        if (os.path.exists(command_path) == False):
            sys.exit("default solr path: {} does not exist! Cannot execute the command. Please check:\n \
            1.Solr is installed under correct path \
            2.This program is run under the correct path".format(command_path))    
        output = subprocess.run(command, shell = True)

    
    def start_server(self):    
        print("we turn on solr server")
        #### we check whether we have correct solr path first
        solr_on = "solrini/bin/solr restart -c -m 8G"
        solr_config = "pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd"
        self.command_execution("solrini/bin/solr", solr_on)
        self.command_execution("src/main/resources/solr", solr_config)  
        print("Solr server is on")


    def stop_server(self):
        subprocess.run("solrini/bin/solr stop -all",shell=True)        

    # We consider 9983 as default local port number
    # If this collection already exists on solr and has the correct number of indexed documents from the previous test,
    # we ask the user whether to overwrite it 
    def index(self,name,input_path,thread_num):
        thread_num = str(thread_num)
        # If this collection already exists, then this path can be found 
        collection_location = "solrini/server/solr/" + name + "_shard1_replica_n1"  

        # If collection already exists on Solr server,we check whether previous executions have the correct number 
        # of indexed doc. If true, we execute user's command, if false, we overwrite the collection 
        if (os.path.exists(collection_location)):
            if (self.check_indexing()  and self.skip_index == "True" ):
                print("We do not overwrite the existing collection,directly jump to Searching step")
                return
            print("We overwrite the existing collection {} ".format(name))  
            command0 = "solrini/bin/solr delete -c {}".format(name)
            self.command_execution("solrini/bin/solr", command0)  

        print("Create data collection on Solr")
        command1 = "solrini/bin/solr create -n anserini -c {}".format(name)     
        self.command_execution("solrini/bin/solr", command1)

        collection, generator = index_options[name]["collection"],index_options[name]["generator"]
        print("\nsolr indexing start for {}, {}, {}, {}, {} \n".format(collection,generator,name,input_path,thread_num))    
        command2 = "sh target/appassembler/bin/IndexCollection -collection {}  -generator {} -threads {} -input {} \
            -solr -solr.index {} -solr.zkUrl localhost:9983  -storePositions -storeDocvectors -storeTransformedDocs \
            ".format(collection,generator,thread_num,input_path,name)
        self.command_execution("target/appassembler/bin/IndexCollection", command2)   

        # check whether index result is as expected 
        if (not self.check_indexing()): raise Exception("Indexing result is not as expected")

    # check whether we have the correct number of indexed documents after indexing step
    def check_indexing(self):
        indexing_result = requests.get("http://localhost:8983/solr/core18/query?q=*:*", auth= ('user','pass')).json()      
        print("Expected indexed number: ",index_options[self.name]["expected_indexed_doc"], "actual indexed number",  
                indexing_result["response"]["numFound"])
        total_indexed_doc = int(indexing_result["response"]["numFound"])
        return total_indexed_doc == int(index_options[self.name]["expected_indexed_doc"]) 
    
    # we consider 8983 as default local port number
    def search(self,name,topic_reader, topic_path):
        command = "sh target/appassembler/bin/SearchSolr -topicreader {} -solr.index {} -solr.zkUrl localhost:9983 -topics {} \
                -output {} ".format(topic_reader,name,topic_path,self.output_path) 
        self.command_execution("target/appassembler/bin/SearchSolr", command)   
        print("searching complete")



    def trec_eval(self,qrel_path, output_path):
        print("\nWe start trec_eval: \n")
        command = "eval/trec_eval.9.0.4/trec_eval -m map -m P.30 {} {}".format(qrel_path,output_path)
        self.command_execution("eval/trec_eval.9.0.4/trec_eval", command)   
        save_result = command + " > this_is_temp_file.txt"    
        subprocess.run(save_result , shell = True)
        keywords = ["map", "P_30"]   
        res = read_file_into_list(keywords)
        MAP,P_30 = res[0].split()[-1], res[1].split()[-1]    
        idea_MAP,idea_P_30 = index_options[self.name]["MAP"],index_options[self.name]["P30"]
        if (MAP != idea_MAP): raise Exception("MAP: {}, which is not equal to expected MAP value {} ".format(MAP,idea_MAP))   
        if (P_30 != idea_P_30): raise Exception("P_30: {}, which is not equal to expected P_30 value {} ".format(P_30,idea_P_30))    
        print("MAP and P_30 are as expected ") 
        if (os.path.exists("this_is_temp_file.txt")) :os.remove("this_is_temp_file.txt")   


    # Integrate solr indexing,searching and trec_eval together 
    def data_collection_process(self,name):
        # These methods require 8 parameters: collectin_name,input_path,thread_num,topic_reader,topic_path,qrel_path
        # collection, generator 
        input_path, thread_num = self.input_path, index_options[name]["thread_num"]  
        topic_reader = index_options[name]["topic_reader"]
        topic_path,qrel_path = index_options[name]["topic_path"],index_options[name]["qrel_path"]
        print("Input: ", name, input_path, thread_num,topic_reader)

        # Check whether collection data was indexed before
        self.index(name,input_path,thread_num)
        self.search(name, topic_reader, topic_path)
        self.trec_eval(qrel_path, self.output_path)   


if __name__ == "__main__":
    solr,collection_name,skip_index, solr_off = None, None, False, False 
    skip_start_server= False
    try:
        parser = argparse.ArgumentParser(description="Solr regression test.")
        parser.add_argument("-collection", type = str, help = "Name of collection to be processed by Solr,this \
                input is required!")
        parser.add_argument("-input_path",type = str, help = "Path of data collection, this input is required!")
        parser.add_argument("-skip_start_server",type = str, help = "If you already start server, input \
                True so we do not need to restart server again")  
        parser.add_argument("-skip_index",type = str, help = "If colletion aleady exists, input True if you want \
                to skip indexing step")    
        parser.add_argument("-solr_off_after_testing", type=str,help="Input True if you want to stop solr \
                server after data processing") 
        args = parser.parse_args()
        collection_name = args.collection
        input_path = args.input_path
        skip_index = args.skip_index
        solr_off,skip_start_server = args.solr_off_after_testing, args.skip_start_server
        solr = SolrClient(collection_name,input_path,skip_index)
    except Exception as e:
        print(e)
        sys.exit("""Invalid input! Run \"python solr_integration_test.py -h\" for further information""")

    """
    It takes all these steps to finish server setup,document indexing,data retrieval and evaluation with BM25
    """

    if (skip_start_server != "True"): solr.start_server()  

    try: solr.data_collection_process(solr.name)
    except Exception as e:print(e)
    

    if (solr_off == "True"): 
        print("Turn off Solr server")
        solr.stop_server()  


    

import json
import os,sys
import subprocess
import argparse



'''
this module can execute indexing and searching on given data collection(eg. robust04,WashingPost)

It must run under parental directory "anserini" with supported solr path

It required 2 parameter(collectin name and collection path) as input to indentify
 all other collection information in
json format "index_options" below.

This instruction can help you to understand how it works:
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

# keyword_search works with trec_eval() to extract actual MAP, P_30 result from "this_is_temp_file.txt" file
def keyword_search(keywords):
    if (os.path.exists("this_is_temp_file.txt") == True):
        with open("this_is_temp_file.txt", "r") as f:
            content = f.read().splitlines()   
        res = []
        for keyword in keywords:
            for n in content:
                if (keyword in n):res.append(n)
    print(res) 
    return res


class Solr_command:
    def __init__(self,name,input_path):
        if (name == None): raise Exception("Need collection name, but receive None")
        if (input_path == None): raise Exception("Need path of collection, but receive None")
        self.name = name 
        self.input_path = input_path
        self.output_path = "run.solr.{}.bm25.topics.{}.301-450.601-700.txt".format(name,name)       

    def command_execution(self, command_path, command):
        print(command)
        if (os.path.exists(command_path) == False):
            sys.exit("default solr path: {} does not exist! Cannot execute the command. Please check:\n \
            1.Solr is installed under correct path \
            2.You run this program under correct path".format(command_path))    
        output = subprocess.run(command, shell = True)

    
    def turn_on_server(self):    
        print("we turn on solr server")
        #### we check whether we have correct solr path first
        solr_on = "solrini/bin/solr restart -c -m 8G"
        solr_config = "pushd src/main/resources/solr && ./solr.sh ../../../../solrini localhost:9983 && popd"
        self.command_execution("solrini/bin/solr", solr_on)
        self.command_execution("src/main/resources/solr", solr_config)  
        print("Solr server is on")


    def turn_off_server(self):
        subprocess.run("solrini/bin/solr stop -all",shell=True)        

    # we consider 9983 as default local port number
    # if this collection already exists on solr and has correct number of indexed document from previous test,we ask user 
    # whether overwrite it, final MAP, P_30 result will be different for some reason. Thus we delete it and re-build it   
    def indexing(self,name,input_path,thread_num):
        thread_num = str(thread_num)
        # if this collection already exists
        collection_location = "solrini/server/solr/" + name + "_shard1_replica_n1"  
        # if check_indexing return False, it mean previous indexed doc is incorrect,then we should overwrite this collection
        # re-execute indexing, searching step.
        if (os.path.exists(collection_location) and self.check_indexing() ):
            response = input("collection already exists on Solr server and indexed doc number is same as expectancy\n \
                    do you want to overwrite it? input [Y]es or [N]o \n")

            if (response == "N"): 
                print("We do not overwrite the existing collection,directly jump to Searching step")
                return
            print("We overwrite the existing collection {}  now".format(name))  
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
        ###subprocess.run(command2, shell = True)  

    # if data collection already exists on solr server, check whether it has correct number of indexed document
    # return true/false
    def check_indexing(self):
        subprocess.run("wget http://localhost:8983/solr/core18/query?q=*:*",shell=True)
        # it takes time to download the file,so we run a loop to make sure 
        while(os.path.exists("query?q=*:*") == False): pass
        ##time.sleep(5)
        with open("query?q=*:*") as json_file:
            indexing_result = json.load(json_file)
        print("Expected indexed number: 595037, actual indexed number", indexing_result["response"]["numFound"])
        total_indexed_doc = int(indexing_result["response"]["numFound"])
        subprocess.run("rm -rf query\?q\=\*\:\*", shell = True)
        return total_indexed_doc == 595037 


    
    # we consider 8983 as default local port number
    def searching(self,name,topic_reader, topic_path):
        command = "sh target/appassembler/bin/SearchSolr -topicreader {} -solr.index {} -solr.zkUrl localhost:9983 -topics {} \
                -output run.solr.{}.bm25.topics.{}.301-450.601-700.txt""".format(topic_reader,name,topic_path,name,name) 
        self.command_execution("target/appassembler/bin/SearchSolr", command)   
        print("searching complete")


    def trec_eval(self,qrel_path, output_path):
        print("\nWe start trec_eval: \n")
        command = "eval/trec_eval.9.0.4/trec_eval -m map -m P.30 {} {}".format(qrel_path,output_path)
        self.command_execution("eval/trec_eval.9.0.4/trec_eval", command)   
        save_result = command + " > this_is_temp_file.txt"    
        subprocess.run(save_result , shell = True)
        keywords = ["map", "P_30"]   
        res = keyword_search(keywords)
        MAP,P_30 = res[0].split()[-1], res[1].split()[-1]    
        idea_MAP,idea_P_30 = index_options[self.name]["MAP"],index_options[self.name]["P30"]
        if (MAP != idea_MAP): raise Exception("MAP: {}, which is not equal to theoritical MAP value {} ".format(MAP,idea_MAP))   
        if (P_30 != idea_P_30): raise Exception("P_30: {}, which is not equal to theoritical P_30 value {} ".format(P_30,idea_P_30))    
        print("MAP and P_30 are both same as theoritical result") 
        subprocess.run("rm -rf this_is_temp_file.txt", shell = True)


    # run solr indexing,search on series of data colletion 
    def data_collection_process(self,name):
        ###It required 8 parameter: collectin_name,input_path,thread_num,topic_reader,topic_path,qrel_path
        ### collection, generator
        input_path, thread_num = self.input_path, index_options[name]["thread_num"]  
        topic_reader = index_options[name]["topic_reader"]
        topic_path,qrel_path = index_options[name]["topic_path"],index_options[name]["qrel_path"]
        print("Input: ", name, input_path, thread_num,topic_reader)

        self.indexing(name,input_path,thread_num)
        self.searching(name, topic_reader, topic_path)
        self.trec_eval(qrel_path, self.output_path)   


if __name__ == "__main__":
    solr,collection_name,solr_off = None, None, False 
    skip_turn_on_server= False
    try:
        parser = argparse.ArgumentParser(description="Solr regression test.")
        parser.add_argument("-collection", type = str, help = "Name of collection to processed by Solr,this \
                input is required!")
        parser.add_argument("-input_path",type = str, help = "Path of data collection, this input is required!")
        parser.add_argument("-skip_turn_on_server",type = str, help = "If you already turned on server, input \
                True so we do not need to re-turn on server again")  
        parser.add_argument("-solr_off_after_testing", type=str,help="Input True if you want to turn off solr \
                server after data processing") 
        args = parser.parse_args()
        collection_name = args.collection
        input_path = args.input_path
        solr_off,skip_turn_on_server = args.solr_off_after_testing, args.skip_turn_on_server
        solr = Solr_command(collection_name,input_path)
    except Exception as e:
        print(e)
        sys.exit("""Invalid input! Run \"python solr_integration_test.py -h\" for further information""")


    """
    it takes all these steps to finish server setup,document indexing,data retrieval,evaluation with BM25
    """

    if (skip_turn_on_server != "True"): solr.turn_on_server()  

    try: solr.data_collection_process(solr.name)
    except Exception as e:print(e)

    if (solr_off == "True"): 
        print("Turn off Solr server")
        solr.turn_off_server()  

    

# -*- coding: utf-8 -*-
"""
Anserini: A Lucene toolkit for replicable information retrieval research

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

"""
TODO:
change if condition in parse_xml to make "< 2017" clear
change create_dataset process so that the generation of new pmids procedure will be included in it
probably filter out those queries that don't contain any included pmids
clean
"""


import xml.etree.ElementTree as ET
import os, json, re, time, sys, argparse, gzip, codecs
from os.path import isfile, join

bioclean_mod = lambda t: re.sub('[.,?;*!%^&_+():-\[\]{}]', '', t.replace('"', '').replace('/', '').replace('\\', '').replace("'", '').replace("-", ' ').strip().lower())

def clean(text):
	return text.strip().replace('\n', ' ').replace('\t', ' ').replace('[','').replace(']','')


# def binary_search(pmids, pmid, low, high):
# 	if high >= low:
# 		mid = low + (high - low)//2
# 		if pmids[mid] == pmid:
# 			return True
# 		elif pmids[mid] > pmid:
# 			return binary_search(pmids, pmid, low, mid-1)
# 		else:
# 			return binary_search(pmids, pmid, mid+1, high)
# 	else:
# 		return False


def binary_search(pmids, pmid, low, high):
	while low <= high:
		mid = low+(high-low)//2
		if pmids[mid] == pmid:
			return True
		elif pmids[mid] < pmid:
			low = mid + 1
		else:
			high = mid - 1
	return False


def parse_xml(fn, pmids, output):
	tree = ET.parse(fn)
	root = tree.getroot()
	infos = {}
	count = 0
	pmids_len = len(pmids) - 1
	for meta in root.findall('PubmedArticle/MedlineCitation'):
		try:
			pmid = meta.find('PMID').text
			article = meta.find('Article')
			year = meta.find('DateCompleted/Year').text
			title = article.find('ArticleTitle')
			abstract = article.find('Abstract/AbstractText')
			real_abstract = ''
			real_title = ''
			# Following McDonald et al. (2018, Deep Relevance Ranking Using Enhanced Document-Query Interactions)
			# we do not use papers with no abstract and papers that are later than 2016
			if int(year) < 2017 and binary_search(pmids, pmid, 0, pmids_len):
				if abstract.text is not None and title.text is not None:
					real_title = title.text
					real_abstract = abstract.text
				elif title.text is None:
					for i in title.iter('i'):
						head, tail = '',''
						if i.text is not None:
							head = i.text
						if i.tail is not None:
							tail = i.tail
						real_title += head + tail + ' '
					real_abstract = abstract.text
				elif abstract.text is None:
					for i in abstract.iter('i'):
						head, tail = '',''
						if i.text is not None:
							head = i.text
						if i.tail is not None:
							tail = i.tail
						real_abstract += head + tail + ' '
					real_title = title.text
				else:
					continue
				# doc_text = '<Title>:'+real_title+' <Abstract>:'+real_abstract
				# doc_text = bioclean_mod(doc_text)
				doc_text = real_title + ' ' + real_abstract
				doc_text = clean(doc_text)
				output_dict = {'id': pmid, 'contents': doc_text}
				output.write(json.dumps(output_dict)+'\n')
				count += 1
		except AttributeError:
			pass
	return count


def create_qrel(fn, outputfn, queryoutput, pmids):
	pmids_len = len(pmids) - 1
	qrel_file = open(outputfn, 'w')
	query_file = open(queryoutput, 'w')
	count = 0
	with open(fn) as jsonfile:
		data = json.load(jsonfile)
		for line in data['questions']:
			query_id = line['id']
			query = line['body']
			query_file.write(query_id+'\t'+query+'\n')
			for doc in line['documents']:
				doc_id = doc.strip().split('/')[-1]
				count+=1
				sys.stdout.write("%d docs processed \r"%count)
				# filter out doc ids that don't contain in pmid ?
				if binary_search(pmids, doc_id, 0, pmids_len):
					qrel_file.write('{} 0 {} 1\n'.format(query_id, doc_id))


def modify_qrel(fn, outputfn, queryoutput, d):
	id_list = []
	for filename in os.listdir(d):
		if filename.endswith('json') and filename.startswith('pro'):
			print(filename)
			with open(join(d,filename)) as jsonfile:
				for line in jsonfile:
					data = json.loads(line)
					idx = data['id']
					id_list.append(idx)
	id_list.sort()
	create_qrel(fn, outputfn, queryoutput, id_list)


def create_queries(fn, outputfn):
	query_file = open(outputfn, 'w')
	with open(fn) as jsonfile:
		data = json.load(jsonfile)
		for line in data['questions']:
			query_id = line['id']
			query = line['body']
			query_file.write(query_id+'\t'+query+'\n')


def create_dataset(args):

	print("Start to process data...")

	pmid_file = args.pmids_file
	filepath = args.collection_path
	outputpath = args.output_folder
	query_file = args.query_file
	max_docs = args.max_docs_per_file

	count = 0
	file_index = 0
	our_pmids = []
	file_names = os.listdir(filepath)
	output = open(join(outputpath,'corpus/docs{:02d}.json'.format(file_index)), 'w')
	pmids = open(pmid_file).read().strip().split('\n')
	for file_name in file_names:
		file_path = join(filepath, file_name)
		if isfile(file_path) and file_path.endswith('.gz'):
			with gzip.open(file_path) as f:
				if count > max_docs:
					file_index += 1
					output.close()
					output = open(join(outputpath,'corpus/docs{:02d}.json'.format(file_index)), 'w')
					count = 0
				count += parse_xml(f, pmids, output)
			# if count % 10000 == 0:
			sys.stdout.write("Processed %d docs \r"%count)
	print("Converted {} docs".format(count))

	# create_qrel(query_file, join(outputpath, 'qrels.test'), join(outputpath, 'query.test'), pmids)
	print("Finished data processing!")


def combine(args):
	filepath = args.collection_path
	outputpath = args.output_folder
	count = 0
	file_index = 0

	file_names = os.listdir(filepath)
	output = open(join(outputpath, 'corpus/docs{:02d}.json'.format(file_index)), 'w')
	for file_name in file_names:
		file_path = join(filepath, file_name)
		if isfile(file_path) and file_path.endswith('json'):
			file_len = len(open(file_path).read().strip().split('\n'))
			count += file_len
			if count > 1000000:
				file_index += 1
				print("created new %dth file\r"%count)
				output = open(join(outputpath, 'corpus/docs{:02d}.json'.format(file_index)), 'w')
				count = 0
			for line in open(file_path):
				output.write(line)


def eliminate_duplication(d):
	id_set = set()
	for filename in os.listdir(d):
		if filename.endswith('json'):
			print(filename)
			pro_doc = open(join(d, 'pro_'+filename), 'w')
			with open(join(d,filename)) as jsonfile:
				prev_len = len(id_set)
				for line in jsonfile:
					data = json.loads(line)
					idx = data['id']
					id_set.add(idx)
					current_len = len(id_set)
					if prev_len+1 == current_len:
						# write to file
						pro_doc.write(line)
					prev_len = current_len
	for filename in os.listdir(d):
		if filename.endswith('json') and filename.startswith('doc'):
			os.remove(join(d, filename))


def check_duplication(d):
	id_set = set()
	id_list = []
	for filename in os.listdir(d):
		if filename.endswith('json') and filename.startswith('pro'):
			print(filename)
			with open(join(d,filename)) as jsonfile:
				for line in jsonfile:
					data = json.loads(line)
					idx = data['id']
					id_set.add(idx)
					id_list.append(idx)
	print("There are {} dup docs.".format(len(id_list)-len(id_set)))


def pubmed_difference(d, pmids_file):
	id_set = set()
	for filename in os.listdir(d):
		file_path = join(d, filename)
		if file_path.endswith('.gz'):
			print(file_path)
			with gzip.open(file_path) as f:
				tree = ET.parse(f)
				root = tree.getroot()
				for meta in root.findall('PubmedArticle/MedlineCitation'):
					pmid = meta.find('PMID').text
					id_set.add(pmid)
	origin_id_set = set(open(pmids_file).read().strip().split('\n'))
	not_contained = origin_id_set - id_set
	wf_name = os.path.join(os.getcwd(), 'need_to_crawl_pmids.txt')
	wf = open(wf_name, 'w')
	print("There are {} articles that don't contain in current dataset".format(len(origin_id_set-id_set)))
					

def clean_all(d):
	for filename in os.listdir(d):
		file_path = join(d, filename)
		wf = open(join(d, 'cleaned_'+filename), 'w')
		if file_path.endswith('.json') and filename.startswith('pro'):
			with open(file_path) as jsonfile:
				for line in jsonfile:
					data = json.loads(line)
					idx = data['id']
					content = data['contents']
					content = content.replace("<Title>:", '').replace('<Abstract>:', ' ')
					# content = bioclean_mod(content)
					output_dict = {'id': idx, 'contents': content}
					wf.write(json.dumps(output_dict)+'\n')
	# for filename in os.listdir(d):
	# 	if filename.endswith('json') and filename.startswith('cleaned'):
	# 		os.remove(join(d, filename))


def eliminate_unincluded_queries(query_file, qrel_file, output_file):
	# find all query ids included in the qrel file
	all_lines = open(qrel_file).read().strip().split('\n')
	all_query_ids = set([line.split(" ")[0] for line in all_lines])
	print(all_query_ids)
	query_f = open(query_file)
	wf = open(output_file, 'w')
	for line in query_f:
		print(line.strip().split('\t')[0])
		if line.strip().split('\t')[0] in all_query_ids:
			wf.write(line)
	query_f.close()
	wf.close()


def calculate_qrel_difference(fall, fpart):
	fall_set = set(open(fall).read().strip().split('\n'))
	fpart_set = set(open(fpart).read().strip().split('\n'))
	wf = open('qrel_diff.txt', 'w')
	for line in (fall_set-fpart_set):
		wf.write(line+'\n')


def test_sequential(pmids):
	start = time.time()
	pmids_len = len(pmids)
	for i in range(1, len(pmids)):
		if pmids[i] <= pmids[i-1]:
			return False
	return True


def test_abs():
	parse_xml('/Users/Gin/Desktop/pubmed19n0949.xml', open('pmids_sorted.txt').read().strip().split('\n'), open('/Users/Gin/Desktop/test_bioasq.json', 'w'))


def test_functions():
	calculate_qrel_difference('/Users/Gin/Documents/Research/IR/anserini/src/main/python/bioasq/qrel_include_all.txt', '/Users/Gin/Documents/Research/IR/anserini/src/main/python/bioasq/anserini_no2017/qrels.test')
	clean_all(join(args.output_folder, 'corpus'))
	modify_qrel(join(os.getcwd(), 'src/main/python/bioasq/bioasq_data/bioasq.test.json'),'/Users/Gin/Documents/Research/IR/anserini/src/main/python/bioasq/anserini_no2017/qrels_filtered_true.test', '/Users/Gin/Documents/Research/IR/anserini/src/main/python/bioasq/temp.txt', '/Users/Gin/Documents/Research/IR/anserini/src/main/python/bioasq/anserini_no2017/corpus/')
	pubmed_difference('/Users/Gin/Documents/Research/IR/dataset/ftp.ncbi.nlm.nih.gov/pubmed/baseline/', 'pmids_sorted.txt')
	check_duplication('/Users/Gin/Documents/Research/IR/anserini/src/main/python/bioasq/anser/corpus')
	create_queries('/Users/Gin/Documents/Research/IR/anserini/src/main/python/bioasq/bioasq_data/bioasq.test.json','/Users/Gin/Documents/Research/IR/anserini/src/main/python/bioasq/query.test')
	count = parse_xml('/Users/Gin/Desktop/pubmed19n0001.xml', open('pmids_sorted.txt').read().strip().split('\n'), open('/Users/Gin/Desktop/test_bioasq.json', 'w'))
	print(count)
	create_qrel('bioasq_data/bioasq.test.json', '/Users/Gin/Documents/Research/IR/anserini/src/main/python/bioasq/qrel_include_all.txt', '/Users/Gin/Documents/Research/IR/anserini/src/main/python/bioasq/temp.txt',open('pmids_sorted.txt').read().strip().split('\n'))
	test_abs()


if __name__ == '__main__':
	parser = argparse.ArgumentParser(description='''Convert PubMed Corpus to Anserini's jsonl files.''')
	parser.add_argument('--collection_path', required=True, help='PubMed corpus file')
	parser.add_argument('--pmids_file', required=True, help='Included pmids')
	parser.add_argument('--query_file', required=True, help='BioASQ query file')
	parser.add_argument('--output_folder', required=True, help='Output folder')
	parser.add_argument('--max_docs_per_file', default=1000000, type=int, help='Maximum range of documents in each jsonl file')

	args = parser.parse_args()
	
	if not os.path.exists(args.output_folder):
		os.makedirs(args.output_folder)
		os.makedirs(os.path.join(args.output_folder, 'corpus'))

	start = time.time()
	
	create_dataset(args)
	print("dataset created!")
	eliminate_duplication(os.path.join(args.output_folder, 'corpus'))
	modify_qrel(args.query_file, join(args.output_folder, 'qrels.test'), join(args.output_folder, 'query.test'), join(args.output_folder, 'corpus'))
	print("finished with {} s".format(time.time() - start))



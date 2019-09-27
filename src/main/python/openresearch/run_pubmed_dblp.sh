#!/bin/bash

until [ $# -eq 0 ]
do
  name=${1:1}; shift;
  if [[ -z "$1" || $1 == -* ]] ; then eval "export $name=true"; else eval "export $name=$1"; shift; fi  
done

python ./src/main/python/openresearch/convert_pubmed_dblp_to_anserini_format.py \
  --output_folder=${output_folder}/anserini_format/pubmed_title \
  --collection_path=${citeomatic_data}/citeomatic-2018-02-12/comparison/pubmed \
  --max_docs_per_file=1000000 \
  --data_type pubmed
python ./src/main/python/openresearch/convert_pubmed_dblp_to_anserini_format.py \
  --output_folder=${output_folder}/anserini_format/pubmed_title_abstract \
  --collection_path=${citeomatic_data}/citeomatic-2018-02-12/comparison/pubmed \
  --max_docs_per_file=1000000 \
  --data_type pubmed \
  --use_abstract_in_query
python ./src/main/python/openresearch/convert_pubmed_dblp_to_anserini_format.py \
  --output_folder=${output_folder}/anserini_format/dblp_title \
  --collection_path=${citeomatic_data}/citeomatic-2018-02-12/comparison/dblp \
  --max_docs_per_file=1000000 \
  --data_type dblp
python ./src/main/python/openresearch/convert_pubmed_dblp_to_anserini_format.py \
  --output_folder=${output_folder}/anserini_format/dblp_title_abstract \
  --collection_path=${citeomatic_data}/citeomatic-2018-02-12/comparison/dblp \
  --max_docs_per_file=1000000 \
  --data_type dblp \
  --use_abstract_in_query

sh ./target/appassembler/bin/IndexCollection -collection JsonCollection \
 -generator LuceneDocumentGenerator -threads 8 -input ${output_folder}/anserini_format/pubmed_title/corpus \
 -index ${output_folder}/lucene-index-pubmed-title -optimize -storePositions -storeDocvectors -storeRawDocs
sh ./target/appassembler/bin/IndexCollection -collection JsonCollection \
 -generator LuceneDocumentGenerator -threads 8 -input ${output_folder}/anserini_format/pubmed_title_abstract/corpus \
 -index ${output_folder}/lucene-index-pubmed-title-abstract -optimize -storePositions -storeDocvectors -storeRawDocs
sh ./target/appassembler/bin/IndexCollection -collection JsonCollection \
 -generator LuceneDocumentGenerator -threads 8 -input ${output_folder}/anserini_format/dblp_title/corpus \
 -index ${output_folder}/lucene-index-dblp-title -optimize -storePositions -storeDocvectors -storeRawDocs
sh ./target/appassembler/bin/IndexCollection -collection JsonCollection \
 -generator LuceneDocumentGenerator -threads 8 -input ${output_folder}/anserini_format/dblp_title_abstract/corpus \
 -index ${output_folder}/lucene-index-dblp-title-abstract -optimize -storePositions -storeDocvectors -storeRawDocs

python ./src/main/python/openresearch/convert_pubmed_to_whoosh_index.py \
  --collection_path ${citeomatic_data}/citeomatic-2018-02-12/comparison/pubmed \
  --whoosh_index ${output_folder}/anserini_format/pubmed_title_abstract/whoosh_index
python ./src/main/python/openresearch/convert_pubmed_to_whoosh_index.py \
  --collection_path ${citeomatic_data}/citeomatic-2018-02-12/comparison/dblp \
  --whoosh_index ${output_folder}/anserini_format/dblp_title_abstract/whoosh_index

python ./src/main/python/openresearch/retrieve.py \
  --index ${output_folder}/lucene-index-pubmed-title \
  --qid_queries ${output_folder}/anserini_format/pubmed_title/queries.test.tsv \
  --valid_docs ${output_folder}/anserini_format/pubmed_title/candidates.txt \
  --output ${output_folder}/anserini_format/pubmed_title/run.test \
  --hits 1000
python ./src/main/python/openresearch/retrieve_with_key_terms.py \
  --index ${output_folder}/lucene-index-dblp-title-abstract \
  --qid_queries ${output_folder}/anserini_format/dblp_title_abstract/queries.test.tsv \
  --valid_docs ${output_folder}/anserini_format/dblp_title_abstract/candidates.txt \
  --output ${output_folder}/anserini_format/dblp_title_abstract/run.keyterms.test \
  --hits 1000 \
  --whoosh_index ${output_folder}/anserini_format/dblp_title_abstract/whoosh_index
python ./src/main/python/openresearch/retrieve.py \
  --index ${output_folder}/lucene-index-pubmed-title-abstract \
  --qid_queries ${output_folder}/anserini_format/pubmed_title_abstract/queries.test.tsv \
  --valid_docs ${output_folder}/anserini_format/pubmed_title_abstract/candidates.txt \
  --output ${output_folder}/anserini_format/pubmed_title_abstract/run.test \
  --hits 1000
python ./src/main/python/openresearch/retrieve.py \
  --index ${output_folder}/lucene-index-dblp-title \
  --qid_queries ${output_folder}/anserini_format/dblp_title/queries.test.tsv \
  --valid_docs ${output_folder}/anserini_format/dblp_title/candidates.txt \
  --output ${output_folder}/anserini_format/dblp_title/run.test \
  --hits 1000
python ./src/main/python/openresearch/retrieve_with_key_terms.py \
  --index ${output_folder}/lucene-index-dblp-title-abstract \
  --qid_queries ${output_folder}/anserini_format/dblp_title_abstract/queries.test.tsv \
  --valid_docs ${output_folder}/anserini_format/dblp_title_abstract/candidates.txt \
  --output ${output_folder}/anserini_format/dblp_title_abstract/run.keyterms.test \
  --hits 1000 \
  --whoosh_index ${output_folder}/anserini_format/dblp_title_abstract/whoosh_index
python ./src/main/python/openresearch/retrieve.py \
  --index ${output_folder}/lucene-index-dblp-title-abstract \
  --qid_queries ${output_folder}/anserini_format/dblp_title_abstract/queries.test.tsv \
  --valid_docs ${output_folder}/anserini_format/dblp_title_abstract/candidates.txt \
  --output ${output_folder}/anserini_format/dblp_title_abstract/run.test \
  --hits 1000

echo "pubmed title"
./eval/trec_eval.9.0.4/trec_eval -mrecip_rank -mmap -mrecall.20,1000 -mP.20  \
 ${output_folder}/anserini_format/pubmed_title/qrels.test ${output_folder}/anserini_format/pubmed_title/run.test
echo "pubmed key terms from title + abstract"
./eval/trec_eval.9.0.4/trec_eval -mrecip_rank -mmap -mrecall.20,1000 -mP.20  \
 ${output_folder}anserini_format/pubmed_title_abstract/qrels.test ${output_folder}anserini_format/pubmed_title_abstract/run.keyterms.test
echo "pubmed title + abstract"
./eval/trec_eval.9.0.4/trec_eval -mrecip_rank -mmap -mrecall.20,1000 -mP.20  \
 ${output_folder}/anserini_format/pubmed_title_abstract/qrels.test ${output_folder}/anserini_format/pubmed_title_abstract/run.test
echo "dblp title"
./eval/trec_eval.9.0.4/trec_eval -mrecip_rank -mmap -mrecall.20,1000 -mP.20  \
 ${output_folder}/anserini_format/dblp_title/qrels.test ${output_folder}/anserini_format/dblp_title/run.test
echo "dblp key terms from title + abstract"
./eval/trec_eval.9.0.4/trec_eval -mrecip_rank -mmap -mrecall.20,1000 -mP.20  \
 ${output_folder}/anserini_format/dblp_title_abstract/qrels.test ${output_folder}/anserini_format/dblp_title_abstract/run.keyterms.test
echo "dblp title + abstract"
./eval/trec_eval.9.0.4/trec_eval -mrecip_rank -mmap -mrecall.20,1000 -mP.20  \
 ${output_folder}/anserini_format/dblp_title_abstract/qrels.test ${output_folder}/anserini_format/dblp_title_abstract/run.test

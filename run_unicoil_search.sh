
export DATA_DIR=~/../../store/scratch/s269lin/project/pyserini

for CORPUS in trec-covid nq climate-fever dbpedia-entity hotpotqa fever arguana cqadupstack-android cqadupstack-gaming cqadupstack-mathematica cqadupstack-programmers cqadupstack-tex cqadupstack-webmasters fiqa quora scidocs signal1m trec-news bioasq cqadupstack-english cqadupstack-gis cqadupstack-physics cqadupstack-stats cqadupstack-unix cqadupstack-wordpress nfcorpus robust04 scifact webis-touche2020
do



# target/appassembler/bin/IndexCollection \
#   -collection JsonVectorCollection \
#   -input ${DATA_DIR}/unicoil_beir_quantized_collections/${CORPUS} \
#   -index indexes/lucene-index.beir-v1.0.0-${CORPUS}-unicoil-noexp/ \
#   -generator DefaultLuceneDocumentGenerator \
#   -threads 16 -impact -pretokenized \
#   > logs/log.beir-v1.0.0-${CORPUS}-unicoil-noexp

echo "=========== ${CORPUS} ===========" >> result.log
python ../pyserini/read_index.py --index ${DATA_DIR}/indexes/lucene-index.${CORPUS}-unicoil-noexp-0shot >> result.log


#  target/appassembler/bin/SearchCollection \
#   -index indexes/lucene-index.beir-v1.0.0-${CORPUS}-unicoil-noexp/ \
#   -topics ${DATA_DIR}/unicoil_beir_queries/topics.beir-v1.0.0-${CORPUS}.test.unicoil-noexp.tsv.gz \
#   -topicreader TsvString \
#   -output runs/run.beir-v1.0.0-${CORPUS}-unicoil-noexp.unicoil-noexp.topics.beir-v1.0.0-${CORPUS}.test.unicoil-noexp.txt \
#   -impact -pretokenized -removeQuery -hits 1000

# echo "=========== ${CORPUS} ===========" >> result.log

# tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.beir-v1.0.0-${CORPUS}.test.txt runs/run.beir-v1.0.0-${CORPUS}-unicoil-noexp.unicoil-noexp.topics.beir-v1.0.0-${CORPUS}.test.unicoil-noexp.txt >> result.log
# tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.beir-v1.0.0-${CORPUS}.test.txt runs/run.beir-v1.0.0-${CORPUS}-unicoil-noexp.unicoil-noexp.topics.beir-v1.0.0-${CORPUS}.test.unicoil-noexp.txt >> result.log
# tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.beir-v1.0.0-${CORPUS}.test.txt runs/run.beir-v1.0.0-${CORPUS}-unicoil-noexp.unicoil-noexp.topics.beir-v1.0.0-${CORPUS}.test.unicoil-noexp.txt >> result.log

done
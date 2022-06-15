
export DATA_DIR=~/../../store/collections/beir-v1.0.0/corpus

for CORPUS in robust04 signal1m trec-news bioasq cqadupstack-android cqadupstack-gaming cqadupstack-mathematica cqadupstack-programmers cqadupstack-tex cqadupstack-webmasters cqadupstack-english cqadupstack-gis cqadupstack-physics cqadupstack-stats cqadupstack-unix cqadupstack-wordpress #trec-covid nq climate-fever dbpedia-entity hotpotqa fever arguana  fiqa quora scidocs  nfcorpus scifact webis-touche2020
do


target/appassembler/bin/IndexCollection \
  -collection BeirFlatCollection \
  -input ${DATA_DIR}/${CORPUS} \
  -index indexes/lucene-index.beir-v1.0.0-${CORPUS}-flat/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw \
  > logs/log.beir-v1.0.0-${CORPUS}-flat


target/appassembler/bin/SearchCollection \
  -index indexes/lucene-index.beir-v1.0.0-${CORPUS}-flat/ \
  -topics src/main/resources/topics-and-qrels/topics.beir-v1.0.0-${CORPUS}.test.tsv.gz \
  -topicreader TsvString \
  -output runs/run.beir-v1.0.0-${CORPUS}-flat.bm25.topics.beir-v1.0.0-${CORPUS}.test.txt \
  -bm25 -removeQuery -hits 1000

echo "=========== ${CORPUS} ===========" >> result.bm25.log

tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 src/main/resources/topics-and-qrels/qrels.beir-v1.0.0-${CORPUS}.test.txt runs/run.beir-v1.0.0-${CORPUS}-flat.bm25.topics.beir-v1.0.0-${CORPUS}.test.txt >> result.bm25.log
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.100 src/main/resources/topics-and-qrels/qrels.beir-v1.0.0-${CORPUS}.test.txt runs/run.beir-v1.0.0-${CORPUS}-flat.bm25.topics.beir-v1.0.0-${CORPUS}.test.txt >> result.bm25.log
tools/eval/trec_eval.9.0.4/trec_eval -c -m recall.1000 src/main/resources/topics-and-qrels/qrels.beir-v1.0.0-${CORPUS}.test.txt runs/run.beir-v1.0.0-${CORPUS}-flat.bm25.topics.beir-v1.0.0-${CORPUS}.test.txt >> result.bm25.log

done
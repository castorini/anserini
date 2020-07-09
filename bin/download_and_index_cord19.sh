#!/bin/sh

DATE=2020-06-12
DATA_DIR=./collections/cord19-"${DATE}"

mkdir "${DATA_DIR}"

wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/document_parses.tar.gz -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/changelog -P "${DATA_DIR}"
wget https://ai2-semanticscholar-cord-19.s3-us-west-2.amazonaws.com/"${DATE}"/metadata.csv -P "${DATA_DIR}"

ls "${DATA_DIR}"/document_parses.tar.gz | xargs -I {} tar -zxvf {} -C "${DATA_DIR}"
rm "${DATA_DIR}"/document_parses.tar.gz

sh target/appassembler/bin/IndexCollection \
  -collection Cord19AbstractCollection -generator Cord19Generator \
  -threads 8 -input "${DATA_DIR}" \
  -index indexes/lucene-index-cord19-abstract-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw -optimize > logs/log.cord19-abstract.${DATE}.txt

sh target/appassembler/bin/IndexCollection \
  -collection Cord19FullTextCollection -generator Cord19Generator \
  -threads 8 -input "${DATA_DIR}" \
  -index indexes/lucene-index-cord19-full-text-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw -optimize > logs/log.cord19-full-text.${DATE}.txt

sh target/appassembler/bin/IndexCollection \
  -collection Cord19ParagraphCollection -generator Cord19Generator \
  -threads 8 -input "${DATA_DIR}" \
  -index indexes/lucene-index-cord19-paragraph-"${DATE}" \
  -storePositions -storeDocvectors -storeContents -storeRaw -optimize > logs/log.cord19-paragraph.${DATE}.txt

cd indexes

tar cvfz lucene-index-cord19-abstract-"${DATE}".tar.gz lucene-index-cord19-abstract-"${DATE}"
tar cvfz lucene-index-cord19-full-text-"${DATE}".tar.gz lucene-index-cord19-full-text-"${DATE}"
tar cvfz lucene-index-cord19-paragraph-"${DATE}".tar.gz lucene-index-cord19-paragraph-"${DATE}"

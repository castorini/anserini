pull:
    git pull origin training_data
compile:
    mvn compile
generate-birthdate:
	mvn exec:java -Dexec.mainClass="io.anserini.util.TrainingDataGenerator" -Dexec.args="-indexPath /datalocal/freebase/freebase-rdf-latest.index.3 -property birthdate -outputFile birthdate.tsv"
index-entity-mentions:
	mvn exec:java -Dexec.mainClass="io.anserini.util.TrainingDataGenerator" -Dexec.args="-indexPath /datalocal/freebase/entity-mentions.index -dataPath /data-fast/datasets/ClueWeb09/ClueWeb09FACC/ClueWeb09_English_1/en0000 -entityIdColNum 7 -entityLabelColNum 2 -docIdColNum 0"

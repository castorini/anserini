#!/bin/sh

mvn clean package appassembler:assemble -DskipTests -Dmaven.javadoc.skip=true

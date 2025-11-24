#!/bin/sh

java -cp `ls target/*-fatjar.jar` ${JAVA_OPTS:--Xms512M -Xmx2G} --add-modules jdk.incubator.vector "$@"


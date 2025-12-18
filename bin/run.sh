#!/bin/sh

java -cp `ls target/*-fatjar.jar` -Xms1G -Xmx32G --add-modules jdk.incubator.vector $@

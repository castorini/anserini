#!/bin/sh

java -cp `ls target/*-fatjar.jar` -Xms512M -Xmx512G --add-modules jdk.incubator.vector $@

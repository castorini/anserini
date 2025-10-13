#!/bin/sh

java -cp `ls target/*-fatjar.jar` -Xms512M -Xmx8G --add-modules jdk.incubator.vector $@

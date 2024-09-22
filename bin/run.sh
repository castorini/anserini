#!/bin/sh

java -cp `ls target/*-fatjar.jar` -Xms512M -Xmx128G --add-modules jdk.incubator.vector $@

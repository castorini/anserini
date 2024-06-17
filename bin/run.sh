#!/bin/sh

java -cp `ls target/*-fatjar.jar` -Xms512M -Xmx16G --add-modules jdk.incubator.vector $@
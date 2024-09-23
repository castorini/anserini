#!/bin/sh

java -cp `ls target/*-fatjar.jar` -Xms512M -Xmx64G --add-modules jdk.incubator.vector $@

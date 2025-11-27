#!/bin/sh

java -cp `ls target/*-fatjar.jar` -Xms512M -Xmx192G --add-modules jdk.incubator.vector $@


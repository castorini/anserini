#!/bin/sh

java -cp `ls target/*-fatjar.jar` -Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector $@ 
# 2>&1 | grep -v "WARNING: Using incubator modules"

# Notes:
# - "WARNING: Using incubator modules: jdk.incubator.vector" cannot be suppressed, so just grep -v it.
#   - the issue with the grep solution is that it interferes with downloading progress bars
# - "SLF4J(I): Connected with provider of type [org.apache.logging.slf4j.SLF4JServiceProvider]": suppress using -Dslf4j

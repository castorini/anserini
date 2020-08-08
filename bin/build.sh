#!/bin/sh

mvn -Dmaven.test.skip=true clean package appassembler:assemble

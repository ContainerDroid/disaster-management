#!/bin/bash

rm -rf output.txt
spark-submit --class com.dm.data.DMDataInterpreter \
		  --master local target/dm-data-interpreter-0.1-jar-with-dependencies.jar \
		  input.txt output.txt

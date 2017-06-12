#!/bin/bash

spark-submit --class com.androidiot.dm.DisasterManagement \
	--master local \
	./target/disaster-management-0.1-jar-with-dependencies.jar

CLASSPATH = "/usr/local/spark-2.1.1-bin-hadoop2.7/jars/spark-core_2.11-2.1.1.jar"

run: wordcount.jar
	spark-submit --class wordcount --master local $^

wordcount.jar: wordcount.class wordcount$$.class
	jar -cvf $@ $^ $(CLASSPATH)

wordcount.class $$.class: wordcount.scala
	scalac -classpath $(CLASSPATH) $^

clean:
	rm -f *.class *.jar

.PHONY: run clean

package com.dm.data

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.log4j.Logger

object DMDataInterpreter {
   def main(args: Array[String]): Unit = {

      if (args.length != 2) {
         println("Usage: wordcount <input-file> <output-file>")
         return
      }

      val sc = new SparkContext("local", "Word Count", "/usr/local/spark-2.1.1-bin-hadoop2.7")
      val input = sc.textFile(args(0))
      val counts = input.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey(_ + _)
      counts.saveAsTextFile(args(1))
   }
}

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark._

object wordcount {
   def main(args: Array[String]) {

      val sc = new SparkContext("local", "Word Count", "/usr/local/spark-2.1.1-bin-hadoop2.7")
      println("Hello from scala!")
   }
}

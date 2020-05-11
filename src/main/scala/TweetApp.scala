//Explanation : https://medium.com/analytics-vidhya/finding-the-most-popular-hashtags-on-twitter-using-spark-streaming-16c3fe09f734

import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.io.Source

object TweetApp {
  def main(args: Array[String]) {

    // Setup the twitter configuration
    setupTwitter()

    // Creating a streaming context which will stream batches of data in every 1 second
    val ssc = new StreamingContext("local[*]", "TweetApp", Seconds(15))

    // Set log level to print errors only
    setupLogging()

    // Create a DStream from twitter
    val tweets = TwitterUtils.createStream(ssc, None)

    // Now extract the text of each status update into DStreams using map()

    val statuses = tweets.map(status => status.getText())

    statuses.foreachRDD(rdd => {
      rdd.foreach(println(_))
    })

    ssc.checkpoint("C://checkpoint")
    ssc.start()
    ssc.awaitTermination()

  }

  def setupLogging() = {
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)
  }

  def setupTwitter() = {
    for (line <- Source.fromFile("src/main/scala/twitter.txt").getLines) {
      val fields = line.split(" ")
      if (fields.length == 2) {
        System.setProperty("twitter4j.oauth." + fields(0), fields(1))
      }
    }
  }
}

//ClassDefFoundError: org/apache/spark/Logging

/*
Download spark-core_2.11-1.5.2.logging.jar and use as --jar option

spark-submit --class com.SentimentTwiteer --packages "org.apache.spark:spark-streaming-twitter_2.11:1.6.3" --jars /root/Desktop/spark-core_2.11-1.5.2.logging.jar /root/Desktop/SentimentTwiteer.jar consumerKey consumerSecret accessToken accessTokenSecret yoursearchTag

java.lang.IllegalArgumentException
 */
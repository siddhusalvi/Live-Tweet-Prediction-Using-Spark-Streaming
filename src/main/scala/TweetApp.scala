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
    val ssc = new StreamingContext("local[*]", "TweetApp", Seconds(10))

    // Set log level to print errors only
    setupLogging()

    // Create a DStream from twitter
    val tweets = TwitterUtils.createStream(ssc, None)

    // Extract text from tweets
    val text = tweets.map(x => x.getText())

    // Extract each word from text
    val words = text.flatMap(x => x.split(" "))

    // Keep the words that start with a hashtag
    val hashtags = words.filter(x => x.startsWith("#"))

    val hashtags_values = hashtags.map(x => (x, 1))

    // Count them over a 5 minute window every 1 second
    val hashtags_count = hashtags_values.reduceByKeyAndWindow((x, y) => x + y, (x, y) => x - y, Seconds(300), Seconds(1))

    // Sort the result by hashtag count in descending order
    val results = hashtags_count.transform(x => x.sortBy(x => x._2, false))

    // Print top 10 results
    results.print

    // Set up a checkpoint
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
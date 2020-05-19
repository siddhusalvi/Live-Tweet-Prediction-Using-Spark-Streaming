import java.nio.charset.StandardCharsets
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object TweetApp {
  def main(args: Array[String]) {
    try {
      val spark = SparkSession.builder().master("local[*]").appName("TweetApp").getOrCreate()
      val sc = spark.sparkContext
      setupTwitter()
      val ssc = new StreamingContext(sc, Seconds(5))
      setupLogging()
      val filter = Array("corona")
      val tweet_stream = TwitterUtils.createStream(ssc, None, filter)
      val statuses = tweet_stream.map(status => status.getText())
      val command = "python src//main//Resources//predictor.py"
      val live_tweets = tweet_stream.map {
        x => new String((x.getText).getBytes(StandardCharsets.US_ASCII), StandardCharsets.US_ASCII).replace("?", "")
      }
      live_tweets.foreachRDD(
        rdd => rdd.foreach(println(_))
      )
      live_tweets.foreachRDD(
        rdd => rdd.pipe(command).collect().foreach(println(_))
      )
      ssc.checkpoint("src//main//Resources//checkpoint")
      ssc.start()
      ssc.awaitTermination()

    } catch {
      case exception1: NoSuchMethodError => println(exception1)
      case exception2: ClassNotFoundException => println(exception2)
      case exception3: InterruptedException => println(exception3)
      case _ => println("Unknown Error occurred!")
    }
  }

  def setupLogging() = {
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)
  }

  def setupTwitter() = {
    System.setProperty("twitter4j.oauth.consumerKey", System.getenv("consumerKey"))
    System.setProperty("twitter4j.oauth.consumerSecret", System.getenv("consumerSecret"))
    System.setProperty("twitter4j.oauth.accessToken", System.getenv("accessToken"))
    System.setProperty("twitter4j.oauth.accessTokenSecret", System.getenv("accessTokenSecret"))
  }
}
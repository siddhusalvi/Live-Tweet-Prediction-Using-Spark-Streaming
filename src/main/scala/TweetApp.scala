import java.nio.charset.StandardCharsets
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}


object TweetApp {
  def main(args: Array[String]) {

    val spark = SparkSession.builder().master("local[*]").appName("TweetApp").getOrCreate()
    val sc = spark.sparkContext
    setupTwitter()

    val ssc = new StreamingContext(sc, Seconds(5))
    setupLogging()

    val filter = Array("corona")
    val tweets = TwitterUtils.createStream(ssc, None, filter)
    val statuses = tweets.map(status => status.getText())
    //    val command = "python src//main//Resources//temp.py"
    val command = "python src//main//Resources//predictor.py"

    val tweet1 = tweets.map {
      x => new String((x.getText).getBytes(StandardCharsets.US_ASCII), StandardCharsets.US_ASCII).replace("?", "")
    }

    tweet1.foreachRDD(
      rdd => rdd.foreach(println(_))
    )
    tweet1.foreachRDD(

      rdd => rdd.pipe(command).collect().foreach(println(_))
    )

    ssc.checkpoint("src//main//Resources//checkpoint")
    ssc.start()
    ssc.awaitTermination()
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

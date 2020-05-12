import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd
import org.apache.spark.sql.SaveMode
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scala.io.Source

object TweetApp {
  def main(args: Array[String]) {

    // Setup the twitter configuration
    setupTwitter()

    val ssc = new StreamingContext("local[*]", "TweetApp", Seconds(15))
    setupLogging()

    val tweets = TwitterUtils.createStream(ssc, None)
    val statuses = tweets.map(status => status.getText())
    val command = "python src//main//Resources//predictor.py"
    statuses.foreachRDD(rdd => {
      rdd.foreach(println(_))
     })
    ssc.checkpoint("src//main//Resources//checkpoint")
    ssc.start()
    ssc.awaitTermination()

  }

  def setupLogging() = {
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)
  }

  def setupTwitter() = {
    for (line <- Source.fromFile("src//main//scala//config.txt").getLines) {
      val fields = line.split(" ")
      if (fields.length == 2) {
        System.setProperty("twitter4j.oauth." + fields(0), fields(1))
      }
    }
  }
}

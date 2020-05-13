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

    val tweets = TwitterUtils.createStream(ssc, None)
    val statuses = tweets.map(status => status.getText())
    val command = "python src//main//Resources//temp.py"
//    val command = "python src//main//Resources//predictor.py"


    statuses.foreachRDD(rdd => {
//      rdd.foreach(println(_))
      rdd.foreachPartition(record=>{
      val data =record.filter(word => word.contains("modi"))
        data.foreach(println(_))
      })
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

    System.setProperty("twitter4j.oauth.consumerKey","ji3ev6wa32KpJDAKpr1tT5mFy")
    System.setProperty("twitter4j.oauth.consumerSecret","xExdIAoRctQOwp6eiD2MfpocNkAJeXRD0neGdlgcP3JHYJKLk1")
    System.setProperty("twitter4j.oauth.accessToken","1186561189653798912-o7TSmfXaS1IHmVUoOyk24EXkO68vqf")
    System.setProperty("twitter4j.oauth.accessTokenSecret","1OAgmzSuexse6KwDoqAI9MT7yRzWyaDeIRGnEsY8Lgk4t")
  }
}

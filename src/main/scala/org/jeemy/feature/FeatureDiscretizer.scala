package org.jeemy.feature

import org.apache.spark.SparkConf
import org.apache.spark.ml.feature.{QuantileDiscretizer, StringIndexer}
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel

/**
 * @User: zhanghuayan
 * @Date: 2020/4/30 7:06 下午
 * @DESC: 特征离散化功能类
 */
object FeatureDiscretizer {
  val spark = SparkSession.builder().config(new SparkConf()).enableHiveSupport().getOrCreate()

  def main(args: Array[String]): Unit = {
    // 参数初始化
    val featureConf = args(0)
    val dataPath = args(1)
    val SPLIT_BIN_NUM = args(2).toInt

    println("\n\n" + featureConf)
    println(dataPath + "\n\n")

    // 读取特征Schema
    val schema = FeatureSchemaParser.readSchema(featureConf)

    var inputCols = FeatureSchemaParser.getContinuousFeatures(featureConf)
    var outputCols = FeatureSchemaParser.getOutputCols(inputCols)

    println(inputCols.mkString("[", ",", "]"))
    println(outputCols.mkString("[", ",", "]"))

    // 读取原始数据
    val dataDF = spark.read.schema(schema).csv(dataPath)
      .drop("deal_type", "lid", "rank")
      .persist(StorageLevel.MEMORY_AND_DISK)
    dataDF.show(10)

    /** **************************  连续特征处理  *******************************/

    // 计算特征分位数
    val discretizer = new QuantileDiscretizer()
      .setInputCols(inputCols)
      .setOutputCols(outputCols)
      .setNumBuckets(SPLIT_BIN_NUM)
      .setHandleInvalid("skip")
    val model = discretizer.fit(dataDF)
    val splitsArray = model.getSplitsArray

    // 输出分桶数组
    println("\n\n")
    for (i <- 0 until splitsArray.length) {
      val splits = splitsArray(i)
      println(inputCols(i) + ":\t" + splits.mkString("[", ",", "]"))
    }

    /** **************************  离散特征处理  *******************************/
    println("\n\n")
    inputCols = FeatureSchemaParser.getDiscreteFeatures(featureConf)
    outputCols = FeatureSchemaParser.getOutputCols(inputCols)
    for (i <- 0 until inputCols.length) {
      val indexer = new StringIndexer()
        .setInputCol(inputCols(i))
        .setOutputCol(outputCols(i))
        .setHandleInvalid("skip")
        .fit(dataDF)

      val indexed = indexer.transform(dataDF)
        .select(inputCols(i), outputCols(i))
        .orderBy(outputCols(i))
        .distinct()

      // 输出离散值映射map
      val ansMap = indexed.take(10000).map {
        row =>
          val srcVal = row.getDouble(0)
          val dstVal = row.getDouble(1)
          srcVal + ":" + dstVal
      }.mkString("[", ",", "]")
      println(inputCols(i) + ":\t" + ansMap + "\n")
    }
  }
}

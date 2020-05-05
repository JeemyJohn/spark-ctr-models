package org.jeemy.models

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.jeemy.utils.SchemaParser

/**
 * @User: zhanghuayan
 * @Date: 2020/4/29 5:06 下午
 * @DESC: 封装所有模型的公共基础接口
 */
trait BaseModel {
  /**
   * spark对象初始化
   */
  val spark = SparkSession.builder().enableHiveSupport().getOrCreate()

  /**
   * 预测函数接口
   */
  def predict(feature: Array[Double]): Double

  /**
   * 训练函数接口
   */
  def fit(trainData: DataFrame): BaseModel

  /**
   * 模型保存
   */
  def saveModel(): Unit

  /**
   * 模型加载
   */
  def loadModel(modelPath: String): BaseModel

  /**
   * 从HDFS路径读取数据
   */
  def readData(path: String, featureConf: String): DataFrame = {
    // 读取数据schema
    val schema = SchemaParser.readSchema(featureConf)
    // 读取HDFS数据为指定schema
    val dataDF: DataFrame = spark.read.schema(schema).csv(path)
    dataDF
  }
}

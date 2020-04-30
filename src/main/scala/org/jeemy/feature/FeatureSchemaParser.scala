package org.jeemy.feature

import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}

/**
 * @User: zhanghuayan
 * @Date: 2020/4/30 6:18 下午
 * @DESC: 根据特征配置文件解析训练数据的Schema
 */
object FeatureSchemaParser {
  val SCHEMA_PARTS = 3

  /**
   * 读取训练数据的特征Schema(FeatureName, DataType, FeatureType)
   */
  def readSchema(featureConf: String): StructType = {
    val structArray = scala.io.Source.fromFile(featureConf).getLines().toArray.filter {
      line => line.trim.length > 0 && !line.trim.charAt(0).equals('#')
    }.map {
      line => line.split(",")
    }.filter {
      parts => parts.length == SCHEMA_PARTS
    }.map {
      parts =>
        val featName = parts(0).trim
        val dataType = parts(1).trim
        val featType = parts(2).trim
        (featName, dataType, featType)
    }.map {
      case (featName, dataType, featType) =>
        dataType match {
          case "double" => StructField(featName, DoubleType, true)
          case "string" => StructField(featName, StringType, true)
          case "int" => StructField(featName, IntegerType, true)
          case _ => StructField(featName, StringType, true)
        }
    }

    val schema = new StructType(structArray)
    schema
  }

  /**
   * 获取所有列名
   */
  def getFeatures(featureConf: String): Array[String] = {
    val features = scala.io.Source.fromFile(featureConf).getLines().toArray.filter {
      line => line.trim.length > 0 && !line.trim.charAt(0).equals('#')
    }.map {
      line => line.split(",").map(x => x.trim)
    }.filter {
      parts => parts.length == SCHEMA_PARTS
    }.map {
      parts =>
        val featName = parts(0).trim
        featName
    }

    features
  }

  /**
   * 获取连续特征列
   */
  def getDiscreteFeatures(featureConf: String): Array[String] = {
    scala.io.Source.fromFile(featureConf).getLines().toArray.filter {
      line => line.trim.length > 0 && !line.trim.charAt(0).equals('#')
    }.map {
      line => line.split(",").map(x => x.trim)
    }.filter {
      parts => parts.length == SCHEMA_PARTS && parts(2).equals("discrete")
    }.map {
      parts =>
        val featName = parts(0).trim
        featName
    }
  }

  /**
   * 获取离散特征列
   */
  def getContinuousFeatures(featureConf: String): Array[String] = {
    scala.io.Source.fromFile(featureConf).getLines().toArray.filter {
      line => line.trim.length > 0 && !line.trim.charAt(0).equals('#')
    }.map {
      line => line.split(",").map(x => x.trim)
    }.filter {
      parts => parts.length == SCHEMA_PARTS && parts(2).equals("continuous")
    }.map {
      parts =>
        val featName = parts(0).trim
        featName
    }
  }

  /**
   * 获取输出列列名
   */
  def getOutputCols(inputCols: Array[String]): Array[String] = {
    val outputCols = inputCols.map(col => col + "_out")
    outputCols
  }
}

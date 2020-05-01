package org.jeemy.models

import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.sql.DataFrame

import scala.collection.mutable.ArrayBuffer

/**
 * @User: zhanghuayan
 * @Date: 2020/4/29 5:45 下午
 * @DESC: Spark LR CTR预测模型实现
 */
class LR extends BaseModel {
  /**
   * LR weights参数，第1位存储Bias
   */
  private var weights: Array[Double] = null

  /**
   * 构造函数
   */
  def this(weights: Array[Double]) {
    this()
    this.weights = weights
  }

  /**
   * 预测函数接口
   */
  def predict(feature: Array[Double]): Double = {
    // 异常检查，预测之前模型必须初始化
    try {
      if (weights == null) {
        throw new Exception("LR weights need be initialized before predict!")
      }
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        return 0.0
    }

    // 计算预测分值并返回
    var score = weights(0)
    for (i <- 0 until feature.length) {
      score += feature(i) * weights(i + 1)
    }
    score
  }

  /**
   * 训练函数接口
   */
  def fit(trainData: DataFrame): BaseModel = {
    val lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.1)
      .setElasticNetParam(0.8)
      .setFeaturesCol("features")
      .setLabelCol("label")
    val lrModel = lr.fit(trainData)

    // 获取训练好的LR模型参数
    val weights = new ArrayBuffer[Double]()
    weights += lrModel.intercept
    weights ++= lrModel.coefficients.toArray

    // 返回LR模型
    new LR(weights.toArray)
  }

  /**
   * 模型保存
   */
  def saveModel(): Unit = {
    // TODO
  }

  /**
   * 模型加载
   */
  def loadModel(modelPath: String): BaseModel = {
    val lr: LR = null;
    // TODO
    lr
  }
}

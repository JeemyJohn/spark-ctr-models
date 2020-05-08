package org.jeemy.models

import ml.dmlc.xgboost4j.scala.spark.XGBoostClassificationModel
import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.sql.DataFrame

/**
 * @User: zhanghuayan
 * @Date: 2020/4/29 5:06 下午
 * @DESC: Spark XGBoostLR CTR预测模型实现
 */
class XGBoostLR extends BaseModel {
  val xgbModel: XGBoostClassificationModel = null
  val lrModel: LogisticRegressionModel = null

  /**
   * 预测函数接口
   */
  def predict(feature: Array[Double]): Double = {
    val score = 0.0
    // TODO
    score
  }

  /**
   * 训练函数接口
   */
  def fit(trainData: DataFrame): BaseModel = {
    val xgbLr: XGBoostLR = null

    xgbLr
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
    val xgbLr: XGBoostLR = null
    // TODO
    xgbLr
  }
}

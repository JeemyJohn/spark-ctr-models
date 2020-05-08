package org.jeemy.models;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jeemy.feature.FeatureConf;
import org.jeemy.feature.FeatureMaker;
import org.jeemy.feature.SparseVector;

import java.io.File;
import java.util.List;

/**
 * @User: zhanghuayan
 * @Date: 2020/4/20 11:28 上午
 */
public class GBDTLRModel {
    /**
     * LR模型解析类
     */
    private LRParser lrParser;

    /**
     * XGBoost模型解析类
     */
    private XGBoostParser xgbParser;

    /**
     * 特征离散化功能类
     */
    private FeatureMaker featureMaker;

    /**
     * 构造函数
     *
     * @param modelConf 模型配置文件
     */
    public GBDTLRModel(String modelConf) {
        // 加载配置文件
        Config conf = ConfigFactory.parseFile(new File(modelConf));

        // 解析LR模型
        List<Double> lrWeights = conf.getDoubleList("lr_model_weights");
        lrParser = new LRParser(lrWeights);

        // 解析XGBoost模型
        String xgbModelPath = conf.getString("xgb_model_path");
        xgbParser = new XGBoostParser(xgbModelPath);

        // 解析特征离散化功能类
        featureMaker = new FeatureMaker(conf);
    }

    /**
     * 批量打分预测
     */
    public double[] predict(double[][] features) {
        double[] scores = new double[features.length];
        for (int i = 0; i < features.length; i++) {
            scores[i] = predict(features[i]);
        }
        return scores;
    }

    /**
     * 预测结果值
     */
    public double predict(double[] denseFeature) {
        // 特征离散化
        SparseVector feature = trans2SparseVector(denseFeature);
        // 获取预测值
        return lrParser.predict(feature);
    }

    /**
     * 获取最终的稀疏编码特征向量：原始特征离散化SparseCode + XGBoost离散化SparseCode
     */
    public SparseVector trans2SparseVector(double[] denseFeature) {
        // 原始特征离散化
        SparseVector vec1 = featureMaker.trans2SparseVector(denseFeature);

        // XGBoost特征离散化
        SparseVector vec2 = xgbParser.trans2SparseVector(denseFeature);

        // 特征合并并返回
        return SparseVector.merge(vec1, vec2);
    }

    /**
     * 保存特征属性及其离散化分桶信息，更新到model.conf
     */
    public void saveAsModelConf(List<FeatureConf> featureConfList) {
        // TODO
    }
}

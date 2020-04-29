package org.jeemy.models;

import org.jeemy.feature.SparseVector;

import java.util.List;

/**
 * @User: zhanghuayan
 * @Date: 2020/4/18 4:31 下午
 */
public class LRParser {
    // LR weights，Bias保存在第一位
    private List<Double> lrWeights;

    /**
     * LR 构造函数
     */
    public LRParser(List<Double> lrWeights) {
        this.lrWeights = lrWeights;
    }

    /**
     * LR 模型预估打分
     */
    public double predict(SparseVector feature) {
        // 进行参数合法性验证
        try {
            if (lrWeights.size() - 1 != feature.size) {
                System.out.println("\nlrWeights size: " + lrWeights.size() + "\nfeature size: " + feature.size);
                throw new Exception("Sparse feature size not match, please check...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 计算预估值
        double score = lrWeights.get(0);
        for (int i = 0; i < feature.indices.length; i++) {
            double value = feature.values[i];
            int index = feature.indices[i] + 1;
            score += value * lrWeights.get(index);
        }
        score = 1.0 / (1.0 + Math.exp(-score));
        return score;
    }


    public List<Double> getLrWeights() {
        return lrWeights;
    }
}

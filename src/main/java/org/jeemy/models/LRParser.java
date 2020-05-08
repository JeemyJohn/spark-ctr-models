package org.jeemy.models;

import org.jeemy.feature.SparseVector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
    public LRParser(String lrModelPath) {
        try {
            File file = new File(lrModelPath);
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader reader = new BufferedReader(streamReader);
            String line = reader.readLine();

            String[] valSplits = line.trim().split(",");
            for (int i = 0; i < valSplits.length; i++) {
                Double weight = Double.parseDouble(valSplits[i]);
                lrWeights.add(weight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

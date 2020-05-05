package org.jeemy.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @User: zhanghuayan
 * @Date: 2020/4/20 2:14 下午
 * @Desc: 单个特征区间信息封装类
 */
public class FeatureConf {
    // 特征名
    public String name;

    // 特征类型（discrete、continuous）
    public String type;

    // 特征离散化区间信息
    public List<String> listInfo;

    // OneHot后特征向量维度空间
    public int dimSize;

    // 连续特征分桶区间数组
    public List<Double> splitBins = null;

    // 离散特征字典映射Map
    public Map<Double, Double> featMap = null;

    /**
     * 特征类型静态标记
     */
    public static String DISCRETE = "discrete";
    public static String CONTINUOUS = "continuous";

    /**
     * 构造函数
     */
    public FeatureConf(String name, String type, List<String> listInfo) {
        this.name = name;
        this.type = type;
        this.listInfo = listInfo;

        // 根据特征类型，判断编码空间大小，并进行不同的处理
        if (type.equals(DISCRETE)) {
            dimSize = listInfo.size();
            featMap = new HashMap<>();
            for (int i = 0; i < listInfo.size(); i++) {
                String[] splits = listInfo.get(i).split(":");
                featMap.put(Double.parseDouble(splits[0]), Double.parseDouble(splits[1]));
            }
        } else {
            dimSize = listInfo.size() - 1;
            splitBins = new ArrayList<>();
            for (int i = 0; i < listInfo.size(); i++) {
                Double val = Double.parseDouble(listInfo.get(i));
                splitBins.add(val);
            }
        }
    }
}

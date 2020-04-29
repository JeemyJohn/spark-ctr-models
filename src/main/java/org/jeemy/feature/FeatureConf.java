package org.jeemy.feature;

import java.util.List;

/**
 * @User: zhanghuayan
 * @Date: 2020/4/20 2:14 下午
 */
public class FeatureConf {
    // 特征名
    public String name;
    // 特征类型（discrete、continuous）
    public String type;
    // 特征离散化区间信息
    public List<Double> values;
    // OneHot后特征向量维度空间（真实维度+1，0号永远用于标记默认值）
    public int size;

    /**
     * 特征类型静态标记
     */
    public static String DISCRETE = "discrete";
    public static String CONTINUOUS = "continuous";

    /**
     * 构造函数
     */
    public FeatureConf(String name, String type, List<Double> values) {
        this.name = name;
        this.type = type;
        this.values = values;
        // 根据特征类型，判断编码空间大小
        if (type.equals(DISCRETE)) {
            size = values.size() + 1;
        } else {
            size = values.size();
        }
    }
}

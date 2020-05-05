package org.jeemy.feature;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @User: zhanghuayan
 * @Date: 2020/4/18 4:30 下午
 */
public class FeatureMaker {
    // 特征离散化信息列表
    private List<FeatureConf> featureConfList = new ArrayList<>();

    /**
     * 构造函数：特征离散化信息列表初始化
     */
    public FeatureMaker(Config conf) {
        // 获取特征对象数组
        List<? extends ConfigObject> objs = conf.getObjectList("feature");
        // 依次解析各个特征
        for (ConfigObject obj : objs) {
            Config config = obj.toConfig();
            String name = config.getString("name");
            String type = config.getString("type");
            List<String> values = config.getStringList("values");
            featureConfList.add(new FeatureConf(name, type, values));
        }
    }

    /**
     * 构造函数：特征离散化信息列表初始化
     */
    public FeatureMaker(String confFile) {
        // 加载配置文件
        Config conf = ConfigFactory.load(confFile);
        // 获取特征对象数组
        List<? extends ConfigObject> objs = conf.getObjectList("feature");
        // 依次解析各个特征
        for (ConfigObject obj : objs) {
            Config config = obj.toConfig();
            String name = config.getString("name");
            String type = config.getString("type");
            List<String> values = config.getStringList("values");
            featureConfList.add(new FeatureConf(name, type, values));
        }
    }

    /**
     * 原始特征离散化获取稀疏特征，LR前半部分使用
     */
    public SparseVector trans2SparseVector(double[] denseFeature) {
        int size = 0;
        int[] indices = new int[featureConfList.size()];
        double[] values = new double[featureConfList.size()];

        // 根据特征类型解析每个特征内部索引
        int startIndex = 0;
        for (int i = 0; i < denseFeature.length; i++) {
            FeatureConf featureConf = featureConfList.get(i);
            size += featureConf.dimSize;
            values[i] = 1.0;
            indices[i] = startIndex + getInnerIndex(denseFeature[i], featureConf);
            startIndex = size;
        }

        return new SparseVector(size, indices, values);
    }

    /**
     * 获取特征OneHot编码值：离散值是字典匹配，连续值是区间匹配的方式
     */
    private int getInnerIndex(double value, FeatureConf featureConf) {
        if (featureConf.type.equals(FeatureConf.DISCRETE)) {
            return featureConf.featMap.get(value).intValue();
        } else if (featureConf.type.equals(FeatureConf.CONTINUOUS)) {
            for (int i = 0; i < featureConf.splitBins.size() - 1; i++) {
                double lValue = featureConf.splitBins.get(i);
                double rValue = featureConf.splitBins.get(i + 1);
                // 分桶区间前闭后开
                if (value >= lValue && value < rValue) {
                    return i;
                }
            }
        }
        // 没匹配到，返回默认值
        return 0;
    }

    /**
     * 类功能测试
     */
    public static void main(String[] args) {
        FeatureMaker featureMaker = new FeatureMaker("model.conf");

        double[] feature = {2.0, 5.0};
        SparseVector vector = featureMaker.trans2SparseVector(feature);
        System.out.println(vector.toString());

        feature = new double[]{-2.0, -5.0};
        vector = featureMaker.trans2SparseVector(feature);
        System.out.println(vector.toString());
    }
}

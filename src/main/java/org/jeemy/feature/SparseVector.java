package org.jeemy.feature;

/**
 * @User: zhanghuayan
 * @Date: 2020/4/18 6:52 下午
 */
public class SparseVector {
    /**
     * 稀疏编码总维度
     */
    public int size;

    /**
     * 非零特征对应的索引值
     */
    public int[] indices;

    /**
     * 非零特征对应的值
     */
    public double[] values;

    /**
     * 默认构造函数
     *
     * @param size    稀疏向量总空间树
     * @param indices 非零特征对应的索引值
     * @param values  非零特征的取值
     */
    public SparseVector(int size, int[] indices, double[] values) {
        // 参数合法性验证
        try {
            for (int i = 0; i < indices.length - 1; i++) {
                if (indices[i] < 0 || indices[i + 1] < 0 || indices[i] >= indices[i + 1]) {
                    System.out.println("index" + i + ":" + indices[i] + ",index" + (i + 1) + ":" + indices[i + 1]);
                    throw new Exception("Invalid parameter indices: all number should be non-negative integer from 0, and in ascending order!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.size = size;
        this.indices = indices;
        this.values = values;
    }

    /**
     * 将两个稀疏向量合并成一个稀疏向量，并返回
     *
     * @param vector1 第一个稀疏向量
     * @param vector2 第二个稀疏向量
     * @return 合并后的稀疏向量
     */
    public static SparseVector merge(SparseVector vector1, SparseVector vector2) {
        int size = vector1.size + vector2.size;
        int nonZeroNum = vector1.indices.length + vector2.indices.length;
        int[] indices = new int[nonZeroNum];
        double[] values = new double[nonZeroNum];

        // 合并vecotr1
        for (int i = 0; i < vector1.indices.length; i++) {
            indices[i] = vector1.indices[i];
            values[i] = vector1.values[i];
        }

        // 合并vecotr2
        int startIndex = vector1.size;
        for (int i = 0; i < vector2.indices.length; i++) {
            indices[vector1.indices.length + i] = startIndex + vector2.indices[i];
            values[vector1.indices.length + i] = vector2.values[i];
        }

        return new SparseVector(size, indices, values);
    }

    @Override
    public String toString() {
        StringBuffer ans = new StringBuffer();

        ans.append("[" + size + ",(");
        for (int i = 0; i < indices.length - 1; i++) {
            ans.append(indices[i] + ",");
        }

        ans.append(indices[indices.length - 1] + "),(");
        for (int i = 0; i < values.length - 1; i++) {
            ans.append(values[i] + ",");
        }
        ans.append(values[values.length - 1] + ")]");

        return ans.toString();

    }

    public static void main(String[] args) {
        // 逻辑验证
        SparseVector vector1 = new SparseVector(50, new int[]{1, 30, 45}, new double[]{1, 2, 3});
        SparseVector vector2 = new SparseVector(50, new int[]{0, 20, 49}, new double[]{4, 5, 6});
        System.out.println("vector1: " + vector1.toString());
        System.out.println("vector2: " + vector2.toString());
        System.out.println("vector3: " + merge(vector1, vector2).toString());
    }
}

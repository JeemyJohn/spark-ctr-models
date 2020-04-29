package org.jeemy.tree;

/**
 * @User: zhanghuayan
 * @Date: 2020/4/18 6:26 下午
 */
public class DecisionTree {
    // 决策树根节点
    public TreeNode rootNode;

    // 决策树叶子节点数
    public int leafNum;

    // 决策树最小叶子节点编号
    public int minLeafIndex;

    // 决策树最大叶子节点编号
    public int maxLeafIndex;

    /**
     * 构造函数
     */
    public DecisionTree() {
        rootNode = null;
        leafNum = 0;
        minLeafIndex = 0;
        maxLeafIndex = 0;
    }

    /**
     * 构造函数
     */
    public DecisionTree(TreeNode rootNode, int leafNum, int minLeafIndex, int maxLeafIndex) {
        this.rootNode = rootNode;
        this.leafNum = leafNum;
        this.minLeafIndex = minLeafIndex;
        this.maxLeafIndex = maxLeafIndex;
    }
}

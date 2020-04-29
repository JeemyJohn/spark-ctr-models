package org.jeemy.tree;

/**
 * @User: zhanghuayan
 * @Date: 2020/4/16 11:16 上午
 */
public class TreeNode {
    // 节点索引号
    public int index;
    // 节点类型
    public NodeType nodeType;
    // 节点分裂特征名
    public String splitFeature;
    // 节点分裂值(如果是叶子节点，那就是预测值)
    public double splitValue;

    // 左右孩子节点索引号以及默认孩子索引号
    public int lNodeIndex;
    public int rNodeIndex;
    public int defaultIndex;

    // 左右孩子节点
    public TreeNode lNode;
    public TreeNode rNode;

    public TreeNode() {
        this.index = 0;
        this.nodeType = NodeType.DEFAULT_NODE;
        this.splitFeature = "";
        this.splitValue = 0.0;
        this.lNodeIndex = 0;
        this.rNodeIndex = 0;
        this.defaultIndex = 0;
        this.lNode = null;
        this.rNode = null;
    }
}

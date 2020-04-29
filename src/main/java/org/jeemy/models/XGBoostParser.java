package org.jeemy.models;

import org.jeemy.feature.SparseVector;
import org.jeemy.tree.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @User: zhanghuayan
 * @Date: 2020/4/16 11:31 上午
 */
public class XGBoostParser {
    /**
     * XGBoost所有决策树
     */
    private List<DecisionTree> trees = new ArrayList<>();

    /**
     * 重建XGBoost模型
     */
    public XGBoostParser(String path) {
        List<List<String>> listLines = loadModelFile(path);
        for (int i = 0; i < listLines.size(); i++) {
            DecisionTree tree = parseTree(listLines.get(i));
            trees.add(tree);
        }
    }

    /**
     * 获取特征向量的XGBoost离散化的结果
     */
    public SparseVector trans2SparseVector(double[] denseFeature) {
        // 稀疏向量
        int size = 0;
        int[] indices = new int[trees.size()];
        double[] values = new double[trees.size()];

        // 构建稀疏向量
        int[] leafIndexes = getPredictLeafs(denseFeature);
        for (int i = 0; i < trees.size(); i++) {
            indices[i] = size + leafIndexes[i];
            values[i] = 1.0;
            // 更新稀疏向量大小
            size += trees.get(i).leafNum;
        }
        return new SparseVector(size, indices, values);
    }

    /**
     * 获取特征向量预测结果在所有决策树上的叶子节点编号
     */
    public int[] getPredictLeafs(double[] feature) {
        // 叶子节点数与树的棵数一致
        int[] leafIndexes = new int[trees.size()];

        for (int i = 0; i < trees.size(); i++) {
            TreeNode node = trees.get(i).rootNode;
            int featureIndex = Integer.parseInt(node.splitFeature.substring(1));
            double featureValue = feature[featureIndex];

            // 只要是内部节点就继续（叶子节点或者默认节点结束）
            while (node.nodeType == NodeType.INNER_NODE) {
                if (featureValue < node.splitValue) {
                    node = node.lNode;
                } else {
                    node = node.rNode;
                }

                // 内部节点和叶子节点展示信息不一样
                if (node.nodeType == NodeType.INNER_NODE) {
                    featureIndex = Integer.parseInt(node.splitFeature.substring(1));
                    featureValue = feature[featureIndex];
                } else if (node.nodeType == NodeType.LEAF_NODE) {
                    // 叶子节点索引保存
                    leafIndexes[i] = (node.index - trees.get(i).minLeafIndex);
                }
            }
        }
        return leafIndexes;
    }

    /**
     * 根据特征实例样本输出所有决策树上的预测路径，isSort为true按照分值倒序输出
     */
    public void showPredictPaths(double[] feature, boolean isSort) {
        List<TreePath> treePaths = new ArrayList<>();

        for (int i = 0; i < trees.size(); i++) {
            TreeNode node = trees.get(i).rootNode;
            int featureIndex = Integer.parseInt(node.splitFeature.substring(1));
            double featureValue = feature[featureIndex];

            StringBuffer sb = new StringBuffer();
            sb.append(String.format("Path %d: (%s,%.4f,%.4f)", i, node.splitFeature, featureValue, node.splitValue));
            // 只要是内部节点就继续（叶子节点或者默认节点结束）
            while (node.nodeType == NodeType.INNER_NODE) {
                if (featureValue < node.splitValue) {
                    node = node.lNode;
                } else {
                    node = node.rNode;
                }

                // 内部节点和叶子节点展示信息不一样
                if (node.nodeType == NodeType.INNER_NODE) {
                    featureIndex = Integer.parseInt(node.splitFeature.substring(1));
                    featureValue = feature[featureIndex];
                    sb.append(String.format(" —> (%s,%.4f,%.4f)", node.splitFeature, featureValue, node.splitValue));
                } else if (node.nodeType == NodeType.LEAF_NODE) {
                    sb.append(String.format(" —> (score: %.4f)", node.splitValue));
                    // 叶子节点说明该决策树路径已经结束
                    treePaths.add(new TreePath(node.splitValue, sb.toString()));
                }
            }
        }

        // 判断是否需要按照分值倒序排序
        if (isSort == true) {
            Collections.sort(treePaths);
        }

        // 依次打印各条路径
        for (TreePath treePath : treePaths) {
            System.out.println(treePath.path);
        }
    }

    /**
     * 根据特征实例样本输出所有决策树上的预测路径
     */
    public void showPredictPaths(double[] feature) {
        showPredictPaths(feature, false);
    }

    /**
     * 根据字符串列表解析单颗决策树
     */
    private DecisionTree parseTree(List<String> lines) {
        Map<Integer, TreeNode> treeNodeMap = new HashMap<>();

        // 解析每个节点数据
        for (String line : lines) {
            TreeNode node = parseLine(line);
            treeNodeMap.put(node.index, node);
        }

        // 叶子节点数 leafNum = maxLeafIndex - minLeafIndex + 1
        int minLeafIndex = Integer.MAX_VALUE;
        int maxLeafIndex = Integer.MIN_VALUE;

        // 构建决策树
        for (TreeNode node : treeNodeMap.values()) {
            // 只有内部节点需要寻找孩子节点，叶子节点不需要处理
            if (node.nodeType == NodeType.INNER_NODE) {
                node.lNode = treeNodeMap.get(node.lNodeIndex);
                node.rNode = treeNodeMap.get(node.rNodeIndex);
            }

            // 更新minLeafIndex/maxLeafIndex
            if (node.nodeType == NodeType.LEAF_NODE) {
                if (maxLeafIndex < node.index) {
                    maxLeafIndex = node.index;
                }

                if (minLeafIndex > node.index) {
                    minLeafIndex = node.index;
                }
            }
        }

        // 索引为零的节点就是根节点
        int leafNum = maxLeafIndex - minLeafIndex + 1;
        return new DecisionTree(treeNodeMap.get(0), leafNum, minLeafIndex, maxLeafIndex);
    }

    /**
     * 将单行字符串解析成单个决策树节点
     */
    private TreeNode parseLine(String line) {
        TreeNode node = new TreeNode();

        // 根据当前节点是否为叶子节点进行不同的解析
        String[] strs;
        if (line.contains("leaf")) {
            strs = line.trim().split(":leaf=");
            node.nodeType = NodeType.LEAF_NODE;
            node.index = Integer.parseInt(strs[0]);
            node.splitValue = Double.parseDouble(strs[1]);
        } else {
            strs = line.trim().split(":\\[|<|\\] yes=|,no=|,missing=");
            node.nodeType = NodeType.INNER_NODE;
            node.index = Integer.parseInt(strs[0]);
            node.splitFeature = strs[1];
            node.splitValue = Double.parseDouble(strs[2]);
            node.lNodeIndex = Integer.parseInt(strs[3]);
            node.rNodeIndex = Integer.parseInt(strs[4]);
            node.defaultIndex = Integer.parseInt(strs[5]);
        }
        return node;
    }

    /**
     * 读取path指向的模型文件，将其转化成一系列字符串数组
     */
    private List<List<String>> loadModelFile(String path) {
        List<List<String>> listLines = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        // 按行读取文件
        try {
            File file = new File(path);
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader reader = new BufferedReader(streamReader);
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith("booster") && lines.size() > 0) {
                    listLines.add(lines);
                    lines = new ArrayList<>();
                } else if (!line.startsWith("booster")) {
                    lines.add(line);
                }
                line = reader.readLine();
            }

            // 最后一棵树
            if (lines.size() > 0) {
                listLines.add(lines);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listLines;
    }
}

package org.jeemy.tree;

/**
 * @User: zhanghuayan
 * @Date: 2020/4/18 5:00 下午
 */
public class TreePath implements Comparable<TreePath> {
    // 在当前决策树路径下的预测结果值
    public double score;
    // 预测路径字符串
    public String path;

    public TreePath() {
        this.score = 0;
        this.path = "";
    }

    public TreePath(double score, String path) {
        this.score = score;
        this.path = path;
    }

    @Override
    public int compareTo(TreePath o) {
        // 倒序排序
        if (o.score > this.score) {
            return 1;
        } else if (o.score < this.score) {
            return -1;
        }
        return 0;
    }
}

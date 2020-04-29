package org.jeemy.tree;

/**
 * @User: zhanghuayan
 * @Date: 2020/4/16  11:40 上午
 */
public enum NodeType {
    DEFAULT_NODE("默认节点", 0),
    LEAF_NODE("叶子节点", 1),
    INNER_NODE("内部节点", 2);

    private String name;
    private int index;

    private NodeType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

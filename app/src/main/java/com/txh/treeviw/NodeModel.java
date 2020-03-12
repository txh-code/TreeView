package com.txh.treeviw;

import java.io.Serializable;
import java.util.LinkedList;

public class NodeModel<T> implements Serializable {

    private T value;
    private LinkedList<NodeModel<T>> childs;
    private int leafCount;
    private boolean isExplosion = true;
    private long tag;

    public NodeModel(T value) {
        this.value = value;
        this.childs = new LinkedList<>();
    }

    public NodeModel(T value, boolean explosion) {
        this.value = value;
        this.isExplosion = explosion;
        this.childs = new LinkedList<>();
    }

    public long getTag() {
        return tag;
    }

    public void setTag(long tag) {
        this.tag = tag;
    }

    public int getLeafCount() {
        return leafCount;
    }

    public boolean isExplosion() {
        return isExplosion;
    }

    public void setExplosion(boolean explosion) {
        isExplosion = explosion;
    }

    public void setLeafCount(int leafCount) {
        this.leafCount = leafCount;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void addChild(NodeModel<T> child) {
        this.childs.add(child);
    }

    public LinkedList<NodeModel<T>> getChilds() {
        return childs;
    }
}

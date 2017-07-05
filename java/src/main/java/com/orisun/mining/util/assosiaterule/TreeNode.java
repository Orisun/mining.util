package com.orisun.mining.util.assosiaterule;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @Description: FP树的节点
 * @Author orisun
 * @Date Jun 23, 2016
 */
class TreeNode {
    private String name;
    private int count;
    private TreeNode parent;
    private List<TreeNode> children;
    private TreeNode nextHomonym;

    public TreeNode() {
    }

    public TreeNode(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public TreeNode getParent() {
        return this.parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public List<TreeNode> getChildren() {
        return this.children;
    }

    public void addChild(TreeNode child) {
        if (getChildren() == null) {
            List<TreeNode> list = new ArrayList<TreeNode>();
            list.add(child);
            setChildren(list);
        } else {
            getChildren().add(child);
        }
    }

    public TreeNode findChild(String name) {
        List<TreeNode> children = getChildren();
        if (children != null) {
            for (TreeNode child : children) {
                if (child.getName().equals(name)) {
                    return child;
                }
            }
        }
        return null;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public void printChildrenName() {
        List<TreeNode> children = getChildren();
        if (children != null) {
            for (TreeNode child : children)
                System.out.print(child.getName() + " ");
        } else
            System.out.print("null");
    }

    public TreeNode getNextHomonym() {
        return this.nextHomonym;
    }

    public void setNextHomonym(TreeNode nextHomonym) {
        this.nextHomonym = nextHomonym;
    }

    public void countIncrement(int n) {
        this.count += n;
    }

}

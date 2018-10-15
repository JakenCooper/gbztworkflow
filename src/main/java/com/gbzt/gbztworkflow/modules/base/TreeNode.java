package com.gbzt.gbztworkflow.modules.base;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    private String nodeid;

    private String text;

    private List<TreeNode> nodes = new ArrayList<TreeNode>();

    public TreeNode() {
    }

    public TreeNode(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TreeNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<TreeNode> nodes) {
        this.nodes = nodes;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }
}

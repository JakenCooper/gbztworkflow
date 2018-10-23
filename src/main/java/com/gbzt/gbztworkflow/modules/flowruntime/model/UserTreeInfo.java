package com.gbzt.gbztworkflow.modules.flowruntime.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserTreeInfo implements Serializable {
   private static final long serialVersionUID = -7371016558713584797L;
   private String id;
   private String parentId;
   private String name;
   private String value;
   private String nodeType;
   private boolean open=false;
   private boolean checked;


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    private List<UserTreeInfo> children = new ArrayList<UserTreeInfo>();
   private UserTreeInfo parent;

    public List<UserTreeInfo> getChildren() {
        return children;
    }

    public void setChildren(List<UserTreeInfo> children) {
        this.children = children;
    }

    public UserTreeInfo getParent() {
        return parent;
    }

    public void setParent(UserTreeInfo parent) {
        this.parent = parent;
    }

    public UserTreeInfo() {
    }

    public UserTreeInfo(String id, String parentId, String name, String value) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.value = value;
    }

    public UserTreeInfo(String id, String parentId, String name, String value,boolean open) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.value = value;
        this.open=open;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}

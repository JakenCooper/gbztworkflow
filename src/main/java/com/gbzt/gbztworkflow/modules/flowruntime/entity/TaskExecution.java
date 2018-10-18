package com.gbzt.gbztworkflow.modules.flowruntime.entity;

import java.util.List;

public class TaskExecution {
    private String id;
    private String procInstId;
    private String taskId;

    private String passUser;
    private String passStr;

    private String assignUser;
    private List<String> assignUserList;

    private boolean claimTag;

    private String executionType;
    private String executionOrder;

    private String oper;
    private String runableType;

}

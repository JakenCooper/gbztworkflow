package com.gbzt.gbztworkflow.modules.flowruntime.entity;

import java.util.Date;
import java.util.List;

public class Task {
    private String id;
    private String taskType;  //usertask or systask
    private String procInstId;
    private String flowId;
    private String nodeId;
    private String nodeName;
    //TODO node def id
    private String nodeDefId;

    private String parentTaskId;

    private String finishType; //single or multi
    private String assignUser;
    private boolean claimTag;
    private String claimUser;
    private List<String> assignUserList;
    private String owner;
    private Date assignTime;
    private Date claimTime;

    private boolean finishTag;
    private Date finishTime;
    private Long duration;
    private String description;
    private boolean withdrawTag;
    private boolean retreatTag;
    private String withdrawDescription;
    private String retreatDescription;

    private String executionType; //block or concurrent
    private String executionOrder; //if block,order for execution

    private boolean qcTag;
    private String qcUser;
    private boolean qcRtnTag;
    private String qcRtnDescription;

    private boolean transTag;
    private String transUser;
    private String transDescription;

    private Integer priority;
    private boolean emergencyTag;

    //TODO 会签逻辑收回或者退回之后额外表设计方式
}

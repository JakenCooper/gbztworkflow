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
    private String nodeDefId;

    private String parentTaskId;

    private String finishType; // af,due to pass & [model]
    private String assignUser; // nf (atten:diff from assignuser in tasks and execs)
    private boolean claimTag; // nf(sub) and af due to claim
    private String claimUser; // nf(sub) and af due to claim
    private List<String> assignUserList; // af
    private String owner; // af,due to finishuser and owner settings [model]
    private Date assignTime; //nf
    private Date claimTime; //nf
    private String passStr; //af

    private boolean finishTag;
    private String finishUser;
    private Date finishTime;
    private Long duration;
    private String description;
    private boolean withdrawTag;
    private boolean retreatTag;
    private String withdrawDescription;
    private String retreatDescription;

    // only useful for "multi" finish type
    private String executionType; // (block or concurrent) [model]
    private String executionOrder; // (if block,order for execution or decide every step)

    private boolean qcTag;
    private String qcUser;
    private boolean qcRtnTag;
    private String qcRtnDescription;

    private boolean transTag;
    private String transUser;
    private String transDescription;

    private Integer priority;
    private boolean emergencyTag;

    private boolean lock;


}

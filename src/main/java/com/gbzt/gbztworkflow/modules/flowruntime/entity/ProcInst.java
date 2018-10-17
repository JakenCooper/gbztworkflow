package com.gbzt.gbztworkflow.modules.flowruntime.entity;

import java.util.Date;

public class ProcInst {
    private String id;
    private String flowId;
    private String bussId;

    private String startUser;
    private Date startTime;

    private boolean finishTag;
    private Date finishTime;
    private Long duration;

    //TODO del_flag del_reason 及其他通用属性
    private String latestTaskId;
    private String latestTaskUser;

    //TODO owner range setting
    private String ownerType;

    //TODO formkey setting
    private String formKey;
}

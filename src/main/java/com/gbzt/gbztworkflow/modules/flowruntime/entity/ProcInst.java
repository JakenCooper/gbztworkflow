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

    private String latestTaskId;
    private String latestTaskUser;

    // owner range setting
    private String ownerType;


    private String formKey;
}

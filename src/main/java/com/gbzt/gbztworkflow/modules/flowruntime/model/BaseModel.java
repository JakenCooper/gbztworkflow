package com.gbzt.gbztworkflow.modules.flowruntime.model;

import com.gbzt.gbztworkflow.consts.ExecResult;

import java.util.Map;

public abstract class BaseModel {

    protected String passUser;
    protected String oper;
    protected String runableType;


    protected Map<String,Object> arguments;

    protected ExecResult execResult;
    protected String token;

}

package com.gbzt.gbztworkflow.modules.flowruntime.model;

import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import com.gbzt.gbztworkflow.utils.LogUtils;

import java.util.Map;

public abstract class BaseModel {

    protected String passUser;
    protected String oper;
    protected String runableType;


    protected Map<String,Object> arguments;

    protected ExecResult execResult;
    protected String token;


    public String getPassUser() {
        return passUser;
    }

    public void setPassUser(String passUser) {
        this.passUser = passUser;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getRunableType() {
        return runableType;
    }

    public void setRunableType(String runableType) {
        this.runableType = runableType;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    public ExecResult getExecResult() {
        return execResult;
    }

    public void setExecResult(ExecResult execResult) {
        this.execResult = execResult;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

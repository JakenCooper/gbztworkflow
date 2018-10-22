package com.gbzt.gbztworkflow.modules.flowruntime.model;

import com.gbzt.gbztworkflow.consts.ExecResult;

import java.io.Serializable;
import java.util.Map;

public abstract class BaseModel implements Serializable {

    private static final long serialVersionUID = -8393847382917284424L;

    protected String passStr;
    protected String passUser;
    protected String bussId;
    protected String formKey;
    protected String flowId;

    protected String oper;
    protected String runableType;


    protected Map<String,String> argMap;

    protected Integer pageNum;
    protected Integer pageSize;
    protected Integer totalPage;

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

    public Map<String, String> getArgMap() {
        return argMap;
    }

    public void setArgMap(Map<String, String> argMap) {
        this.argMap = argMap;
    }

    public String getPassStr() {
        return passStr;
    }

    public void setPassStr(String passStr) {
        this.passStr = passStr;
    }

    public String getBussId() {
        return bussId;
    }

    public void setBussId(String bussId) {
        this.bussId = bussId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
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

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }
}

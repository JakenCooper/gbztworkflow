package com.gbzt.gbztworkflow.modules.base;

import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.flowruntime.model.BaseModel;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import com.gbzt.gbztworkflow.utils.LogUtils;

public class BaseService {

    protected  <T> ExecResult<T> buildResult(boolean charge, String message, T result){
        return CommonUtils.buildResult(charge,message,result);
    }

    protected final BaseModel buildResult(BaseModel model, Integer level, String logType, String logMessage, Throwable e,
                                          boolean charge, String rtnmessage, Object result){
        switch (level){
            case 1:logger.debug(LogUtils.getMessage(logType,logMessage));model.setExecResult(buildResult(charge,rtnmessage,result)); break;
            case 2:logger.info(LogUtils.getMessage(logType,logMessage));model.setExecResult(buildResult(charge,rtnmessage,result)); break;
            case 3:logger.warn(LogUtils.getMessage(logType,logMessage));model.setExecResult(buildResult(charge,rtnmessage,result)); break;
            case 4:logger.error(LogUtils.getMessage(logType,e.getMessage()),e);model.setExecResult(buildResult(charge,rtnmessage,result)); break;
            default:break;
        }
        return model;
    }

    protected final BaseModel buildResult(BaseModel model,boolean charge,String rtnmessage,Object result){
        model.setExecResult(buildResult(charge,rtnmessage,result));
        return model;
    }
}

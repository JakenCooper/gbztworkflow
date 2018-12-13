package com.gbzt.gbztworkflow.modules.base;

import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.flowruntime.model.BaseModel;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import com.gbzt.gbztworkflow.utils.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;

public class BaseService {

    private Logger logger ;

    protected  <T> ExecResult<T> buildResult(boolean charge, String message, T result){
        return CommonUtils.buildResult(charge,message,result);
    }

    protected void setLogger(Logger logger){
        this.logger = logger;
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

    protected final boolean isBlank(String target){
        return StringUtils.isBlank(target);
    }

    protected final boolean isBlank(Collection target){
        if(target == null){
            return true;
        }
        if(target.size() == 0){
            return true;
        }
        return false;
    }

    protected final boolean isBlank(Map target){
        if(target == null){
            return true;
        }
        if(target.keySet().size() == 0){
            return true;
        }
        return false;
    }

    protected final boolean isNotBlank(String target){
        return StringUtils.isNotBlank(target);
    }

    protected final boolean isNotBlank(Collection target){
        if(target != null && target.size() != 0){
            return true;
        }
        return false;
    }

    protected final boolean isNotBlank(Map target){
        if(target != null && target.keySet().size() != 0){
            return true;
        }
        return false;
    }
}

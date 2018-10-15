package com.gbzt.gbztworkflow.modules.base;

import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.utils.CommonUtils;

public class BaseService {

    protected  <T> ExecResult<T> buildResult(boolean charge, String message, T result){
        return CommonUtils.buildResult(charge,message,result);
    }
}

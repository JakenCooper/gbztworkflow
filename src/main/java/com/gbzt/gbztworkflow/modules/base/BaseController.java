package com.gbzt.gbztworkflow.modules.base;

import com.gbzt.gbztworkflow.utils.CommonUtils;
import org.springframework.http.ResponseEntity;

public class BaseController {

    protected <T> ResponseEntity buildResp(Integer code, T t){
        return CommonUtils.buildResp(code,t);
    }
}

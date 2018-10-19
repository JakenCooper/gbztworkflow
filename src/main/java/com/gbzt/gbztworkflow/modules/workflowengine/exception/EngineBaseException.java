package com.gbzt.gbztworkflow.modules.workflowengine.exception;

import com.gbzt.gbztworkflow.consts.AppConst;

public class EngineBaseException extends RuntimeException {

    private static final long serialVersionUID = -9011549517941289332L;

    public EngineBaseException(String exceptionType, String description, String message,Throwable throwable) {
        super(genMessage(exceptionType,description,message),throwable);
        if(AppConst.STDOUT_SWITCH){
            throwable.printStackTrace();
        }
    }

    public EngineBaseException(String exceptionType, String description) {
        super(genMessage(exceptionType,description,null));
    }

    private static String genMessage(String exceptionType, String description, String message){
        StringBuffer sb = new StringBuffer();
        if(message != null){
            sb.append("[").append(exceptionType).append("]------> ").append(description)
                    .append(" ===== ").append(message);
        }else{
            sb.append("[").append(exceptionType).append("]------> ").append(description);
        }
        if(AppConst.STDOUT_SWITCH){
            System.out.println(sb.toString());
        }
        return sb.toString();
    }
}

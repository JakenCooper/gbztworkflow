package com.gbzt.gbztworkflow.utils;

import com.gbzt.gbztworkflow.consts.AppConst;

public class LogUtils {

    public static String getMessage(String type,String message){
        StringBuffer sb = new StringBuffer();
        sb.append("[").append(type).append("]------> ").append(message);
        if(AppConst.STDOUT_SWITCH){
            System.out.println(sb.toString());
        }
        return sb.toString();
    }

}

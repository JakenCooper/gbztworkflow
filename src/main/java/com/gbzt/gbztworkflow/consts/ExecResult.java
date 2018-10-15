package com.gbzt.gbztworkflow.consts;

public class ExecResult<T> {

    public final boolean charge;

    public final String message;

    public final T result;

    public ExecResult(boolean charge,String message,T result){
        this.charge = charge;
        this.message = message;
        this.result = result;
    }


}

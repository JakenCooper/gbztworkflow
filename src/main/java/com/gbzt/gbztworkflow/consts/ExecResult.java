package com.gbzt.gbztworkflow.consts;

import java.io.Serializable;

public class ExecResult<T> implements Serializable {

    private static final long serialVersionUID = -8129066765358809843L;

    public final boolean charge;

    public final String message;

    public final T result;

    public ExecResult(boolean charge,String message,T result){
        this.charge = charge;
        this.message = message;
        this.result = result;
    }


}

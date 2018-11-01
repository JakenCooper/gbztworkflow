package com.gbzt.gbztworkflow.modules.flowruntime.service;

import com.gbzt.gbztworkflow.modules.flowruntime.model.TaskModel;
import org.springframework.stereotype.Component;

public interface IRuntimeService {
    /*
     *  rtn : List<Map> ensensial attrs of nodes
     * */
    public TaskModel getNextStep(TaskModel model);

    /*
     * rtn : procInstId
     * */
    public TaskModel startProc(TaskModel model);

    /*
     * rtn : List<Map> ensensial attrs of tasks
     * */
    public TaskModel finishTask(TaskModel model);

    /*
     *  rtn : List<Map> ensensial attrs of tasks
     * */
    public TaskModel getUndo(TaskModel model);

    /*
     *  rtn : List<UserTreeInfo>
     * */
    public TaskModel getUserNodeData(TaskModel model);

    /*
     *  rtn : List<Map> ensensial attrs of tasks
     * */
    public TaskModel getHistTask(TaskModel model);

    /*
     *  rtn : List<Map> ensensial attrs of tasks
     * */
    public TaskModel getProcHistoric(TaskModel model);

    /*
     *  retreatSubmitTag : false
     *  rtn : boolean
     * */
    public TaskModel canRetreat(TaskModel model);

    /*
     *  retreatSubmitTag : true
     *  rtn : String
     * */
    public TaskModel retreatSubmit(TaskModel model);
}

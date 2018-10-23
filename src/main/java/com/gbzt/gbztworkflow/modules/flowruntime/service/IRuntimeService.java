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
}

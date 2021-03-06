package com.gbzt.gbztworkflow.modules.flowruntime.service;

import com.gbzt.gbztworkflow.modules.affairConfiguer.entity.AffairConfiguer;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowruntime.model.TaskModel;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import org.springframework.stereotype.Component;

import java.util.List;

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

    /*
     *  rtn : List<Map<String,String>> ensensial attrs of flows
     * */
    public TaskModel getBussTable(TaskModel model);

    /*获取事务查询配置*/
    public List<TaskModel> getAffairConfiguerList(TaskModel taskModel);

    public TaskModel findFlowByFlowName(TaskModel taskModel);
    /*end*/

    /*文件上传*/
    public TaskModel uploadFileInfo(TaskModel taskModel);

    //根据流程实例id查询附件
    public List<TaskModel> findCommonFileByProcInsId(TaskModel taskModel);

    //删除附件数据库信息
    public TaskModel delCommonFileByproInsId(TaskModel taskModel);

    //寻找结束节点
    public TaskModel findNodeByFlowIdAndEndNode(TaskModel taskModel);

    //寻找开始节点
    public TaskModel findNodeByFlowIdAndBeginNode(TaskModel taskModel);

    //根据结束节点id和当前节点名称 查询是否有结束连线
    public TaskModel findLineByEndNodeIdAndBeginNodeId(TaskModel taskModel);

    //寻找节点名称对应Id
    public TaskModel findNodeByNodeDefIdAndFlowId(TaskModel taskModel);

    //查询转出节点
    public List<TaskModel> findNodeByFlowIdAndDelTagAndTransferOut(TaskModel taskModel);

    public TaskModel findAdviseBussTblNameByProcInstId(TaskModel taskModel);
}

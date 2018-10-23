package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.UserNodePriv;
import com.gbzt.gbztworkflow.modules.flowdefination.service.UserNodePrivService;
import com.gbzt.gbztworkflow.modules.flowruntime.model.UserTreeInfo;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class GetUserNodeData extends EngineBaseExecutor {

    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_GETUSERNODEDTA_SYNC;

    private Logger logger = Logger.getLogger(GetUserNodeData.class);
    private static String LOGGER_TYPE_PREFIX = "GetUserNodeData,";

    public static class GetUserNodeDataArg implements IEngineArg{
        public TaskExecution execution;
        public UserNodePrivService userNodePrivService;
    }

    @Override
    public EngineTask generateDefaultEngineTask(IEngineArg iarg, Object externalArg) {
        GetUserNodeData.GetUserNodeDataArg  arg = (GetUserNodeData.GetUserNodeDataArg)iarg;
        EngineTask engineTask = super.generateDefaultEngineTask(TASK_TYPE,iarg);
        return engineTask;
    }

    @Override
    public void preHandleTask(EngineTask task) throws EngineAccessException {
        GetUserNodeData.GetUserNodeDataArg  arg = (GetUserNodeData.GetUserNodeDataArg)task.getArgs();
        if(isBlank(arg.execution.nodeId)){
            throw new EngineAccessException("no nodeid found in request");
        }
    }

    @Override
    public String executeEngineTask(EngineTask task) throws EngineRuntimeException {
        GetUserNodeData.GetUserNodeDataArg  arg = (GetUserNodeData.GetUserNodeDataArg)task.getArgs();
        TaskExecution execution = arg.execution;
        UserNodePriv userNodePriv = new UserNodePriv();
        userNodePriv.setFlowId(execution.flowId);
        userNodePriv.setNodeId(execution.nodeId);
        List<UserNodePriv> privindb = arg.userNodePrivService.findUserNodePrivsByNodeId(execution.nodeId);
        if(isBlank(privindb)){
            task.setExecutedResult(new ArrayList<UserTreeInfo>());
            return "success";
        }
        fdafds
        // TODO cache.
        // use nodeType of first node as nodeType of root node.
        userNodePriv.setNodeType(privindb.get(0).getNodeType());
        // construct loginNames and use  this list to filter users.
        List<String> loginNames = new ArrayList<String>();
        for(UserNodePriv tempPriv : privindb){
            loginNames.add(tempPriv.getLoginName());
        }
        userNodePriv.setLoginNames(loginNames);
        task.setExecutedResult(arg.userNodePrivService.getAllUserInfo(userNodePriv));
        return "success";
    }

    @Override
    public List<UserTreeInfo> handleCallback(EngineTask task) throws EngineRuntimeException {
        return (List<UserTreeInfo>)task.getExecutedResult();
    }



}

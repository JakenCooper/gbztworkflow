package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.UserNodePriv;
import com.gbzt.gbztworkflow.modules.flowruntime.model.UserTreeInfo;
import com.gbzt.gbztworkflow.modules.redis.service.JedisService;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.utils.SimpleCache;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class GetUserNodeData extends EngineBaseExecutor {

    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_GETUSERNODEDTA_SYNC;

    private Logger logger = Logger.getLogger(GetUserNodeData.class);
    private static String LOGGER_TYPE_PREFIX = "GetUserNodeData,";

    public static class GetUserNodeDataArg extends EngineBaseArg implements IEngineArg{
        public TaskExecution execution;
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

        if(AppConst.REDIS_SWITCH){
            List<UserTreeInfo> cacheinfos = arg.nodeUserPrivCacheService.getFromCache(arg.jedisService,execution.nodeId);
            task.setExecutedResult(cacheinfos);
            return "success";
        }

        List<UserTreeInfo> cacheinfos = (List<UserTreeInfo>)SimpleCache.getFromCache(JedisService.CACHE_KEY_PREFIX_USER_NODE_PRIV
                +execution.nodeId);
        if(isNotBlank(cacheinfos)){
            task.setExecutedResult(cacheinfos);
            return "success";
        }
        UserNodePriv userNodePriv = new UserNodePriv();
        userNodePriv.setFlowId(execution.flowId);
        userNodePriv.setNodeId(execution.nodeId);
        
        UserNodePriv privindb = arg.nodeUserPrivService.findUseroneNodePrivsByNodeId(execution.nodeId);
        if("".equals(privindb) || privindb == null){
            task.setExecutedResult(new ArrayList<UserTreeInfo>());
            return "success";
        }

        userNodePriv.setNodeType(privindb.getNodeType());
        // construct loginNames and use  this list to filter users.
        String names = privindb.getLoginName();
        List<String> loginNames = new ArrayList<String>();
        if(names.contains(",")){
            String arr[] = names.split(",");
            for(int i=0;i<arr.length;i++){
                loginNames.add(arr[i]);
            }
        }
        userNodePriv.setLoginNames(loginNames);
        List<UserTreeInfo> resultTreeInfos =arg.nodeUserPrivService.getAllUserInfo(userNodePriv);
        task.setExecutedResult(resultTreeInfos);
        return "success";
    }

    @Override
    public List<UserTreeInfo> handleCallback(EngineTask task) throws EngineRuntimeException {
        return (List<UserTreeInfo>)task.getExecutedResult();
    }



}

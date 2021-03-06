package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.redis.service.JedisService;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.utils.SimpleCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetNextStep extends EngineBaseExecutor {
    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_GETNEXTSTEP_SYNC;

    private Logger logger = Logger.getLogger(GetNextStep.class);
    private static String LOGGER_TYPE_PREFIX = "GetNextStep,";

    public static class GetNextStepArg extends EngineBaseArg implements IEngineArg {
        private String[] requiredArg = new String[]{"flowName","taskId"};
        public TaskExecution execution;

        private Flow flowInst;
    }

    @Override
    public EngineTask generateDefaultEngineTask(IEngineArg iarg, Object externalArg) {
        GetNextStep.GetNextStepArg arg = (GetNextStep.GetNextStepArg)iarg;
        EngineTask engineTask = super.generateDefaultEngineTask(TASK_TYPE,arg);
        return engineTask;
    }

    @Override
    public void preHandleTask(EngineTask task) throws EngineAccessException {
        GetNextStep.GetNextStepArg arg = (GetNextStep.GetNextStepArg)task.getArgs();
        Flow flowInst =null;
        if(!AppConst.REDIS_SWITCH) {
            flowInst = arg.definationService.getFlowByName(arg.execution.flowName);
        }else{
            flowInst = arg.jedisService.findFlowByIdOrName(null,arg.execution.flowName);
        }
        flowInst = super.getFlowComplete(arg.definationService,arg.definationCacheService,flowInst.getId());
        if(flowInst == null || flowInst.getId() ==null ||flowInst.getStartNode() == null){
            throw new EngineAccessException("flow not exist or no start node.");
        }
        arg.flowInst = flowInst;
    }

    @Override
    public String executeEngineTask(EngineTask task) throws EngineRuntimeException {
        GetNextStep.GetNextStepArg arg = (GetNextStep.GetNextStepArg)task.getArgs();
        TaskExecution execution = arg.execution;

        List<Map<String,String>> result = new ArrayList<Map<String,String>>();
        Node targetNode = null;
        Flow flowInst = arg.flowInst;
        if(StringUtils.isBlank(arg.execution.taskId)){
            targetNode = flowInst.getStartNode();
        }else{
            Task taskObj = null;
            if(!AppConst.REDIS_SWITCH) {
                taskObj = arg.taskDao.findOne(execution.taskId);
            }else{
                taskObj = arg.jedisService.findTaskById(execution.taskId);
            }
            targetNode = arg.flowInst.getNodeIdMap().get(taskObj.getNodeId());
        }
        for(Node node : targetNode.getNextNodes()){
            Map<String,String> nodeMap = new HashMap<>();
            nodeMap.put("nodeId",node.getId());
            nodeMap.put("nodeDefId",node.getNodeDefId());
            nodeMap.put("nodeName",node.getName());
            nodeMap.put("flowId",node.getFlowId());
            nodeMap.put("startNodeName",flowInst.getStartNode().getName());
            result.add(nodeMap);
        }
        task.setExecutedResult(result);

        return "success";
    }

    @Override
    public List<Map<String,String>> handleCallback(EngineTask task) throws EngineRuntimeException {
        return (List<Map<String,String>>)task.getExecutedResult();
    }

}

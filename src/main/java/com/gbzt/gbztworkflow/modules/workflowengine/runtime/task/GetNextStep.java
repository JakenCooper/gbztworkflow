package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.TaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
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

    public static class GetNextStepArg implements IEngineArg {
        private String[] requiredArg = new String[]{"flowName","taskId"};
        public DefinationService definationService;
        public TaskExecution execution;
        public TaskDao taskDao;

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
        if(StringUtils.isBlank(arg.execution.taskId)){
            Flow flowInst = arg.definationService.getFlowByName(arg.execution.flowName);
            // TODO not necessary
            arg.definationService.generateDetailDefination(flowInst.getId());
            //TODO cache oper..
            flowInst = (Flow)SimpleCache.getFromCache(SimpleCache.CACHE_KEY_PREFIX_FLOW_DETAIL+flowInst.getId());
            if(flowInst == null || flowInst.getStartNode() == null){
                throw new EngineAccessException("flow not exist or no start node.");
            }
            arg.flowInst = flowInst;
        }
    }

    @Override
    public String executeEngineTask(EngineTask task) throws EngineRuntimeException {
        GetNextStep.GetNextStepArg arg = (GetNextStep.GetNextStepArg)task.getArgs();
        TaskExecution execution = arg.execution;

        List<Map<String,String>> result = new ArrayList<Map<String,String>>();
        Node targetNode = null;
        if(StringUtils.isBlank(arg.execution.taskId)){
            Flow flowInst = arg.flowInst;
            targetNode = flowInst.getStartNode();
        }else{
            Task taskObj = arg.taskDao.findOne(execution.taskId);
            targetNode = arg.flowInst.getNodeIdMap().get(taskObj.getNodeId());
        }
        for(Node node : targetNode.getNextNodes()){
            Map<String,String> nodeMap = new HashMap<>();
            nodeMap.put("nodeId",node.getId());
            nodeMap.put("nodeDefId",node.getNodeDefId());
            nodeMap.put("nodeName",node.getName());
            nodeMap.put("flowId",node.getFlowId());
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

package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineTaskTemplateFactory;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import com.gbzt.gbztworkflow.utils.SimpleCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetNextStep extends EngineBaseExecutor {
    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_GETNEXTSTEP_SYNC;

    private Logger logger = Logger.getLogger(GetNextStep.class);
    private static String LOGGER_TYPE_PREFIX = "GetNextStep,";

    public static class GetNextStepArg implements IEngineArg {
        private String[] requiredArg = new String[]{"flowName","procInstId"};
        public DefinationService definationService;
        public TaskExecution execution;

        private Flow flowInst;
    }

    @Override
    public EngineTask generateDefaultFtpTask(IEngineArg iarg, Object externalArg) {
        EngineTask engineTask = new EngineTask();
        EngineTask templateTask = EngineTaskTemplateFactory.buildEngineTaskByTemplate(TASK_TYPE);
        try {
            BeanUtils.copyProperties(engineTask,templateTask);
        } catch (BeansException e) {
            e.printStackTrace();
        }
        GetNextStep.GetNextStepArg arg = (GetNextStep.GetNextStepArg)iarg;
        engineTask.setArgs(arg);
        engineTask.setTaskId(CommonUtils.genUUid());
        return engineTask;
    }

    @Override
    public void preHandleTask(EngineTask task) throws EngineAccessException {
        GetNextStep.GetNextStepArg arg = (GetNextStep.GetNextStepArg)task.getArgs();
        if(StringUtils.isBlank(arg.execution.procInstId)){
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

        List<Map<String,String>> result = new ArrayList<Map<String,String>>();
        if(StringUtils.isBlank(arg.execution.procInstId)){
            Flow flowInst = arg.flowInst;
            Node startNode = flowInst.getStartNode();

            for(Node node : startNode.getNextNodes()){
                Map<String,String> nodeMap = new HashMap<>();
                nodeMap.put("nodeId",node.getId());
                nodeMap.put("nodeDefId",node.getNodeDefId());
                nodeMap.put("nodeName",node.getName());
                nodeMap.put("flowId",node.getFlowId());
                result.add(nodeMap);
            }
            task.setExecutedResult(result);
        }else{
            //TODO procinst fetch
            task.setExecutedResult(result);
        }


        return "success";
    }

    @Override
    public List<Map<String,String>> handleCallback(EngineTask task) throws EngineRuntimeException {
        return (List<Map<String,String>>)task.getExecutedResult();
    }

}

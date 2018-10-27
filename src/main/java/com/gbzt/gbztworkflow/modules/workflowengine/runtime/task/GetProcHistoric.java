package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.TaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import org.apache.log4j.Logger;

import java.util.*;

public class GetProcHistoric extends EngineBaseExecutor {

    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_GETPROCHISTORIC_SYNC;

    private Logger logger = Logger.getLogger(GetProcHistoric.class);
    private static String LOGGER_TYPE_PREFIX = "GetProcHistoric,";

    public static class GetProcHistoricArg implements IEngineArg{
        public DefinationService definationService;
        public TaskDao taskDao;
        public TaskExecution execution;
    }
    @Override
    public EngineTask generateDefaultEngineTask(IEngineArg iarg, Object externalArg) {
        GetProcHistoric.GetProcHistoricArg arg = (GetProcHistoric.GetProcHistoricArg)iarg;
        EngineTask task = super.generateDefaultEngineTask(TASK_TYPE,arg);
        return task;
    }

    @Override
    public void preHandleTask(EngineTask task) throws EngineAccessException {
        GetProcHistoric.GetProcHistoricArg arg = (GetProcHistoric.GetProcHistoricArg)task.getArgs();
        if(isBlank(arg.execution.procInstId)){
            throw new EngineAccessException("no procinstid detected..");
        }
    }

    @Override
    public String executeEngineTask(EngineTask task) throws EngineRuntimeException {
        GetProcHistoric.GetProcHistoricArg arg = (GetProcHistoric.GetProcHistoricArg)task.getArgs();
        TaskExecution execution = arg.execution;
        List<Task> finalTasks = new ArrayList<>();
        if(!execution.childTaskTag){
            // historic runtime infos for proc.
            finalTasks  = arg.taskDao.findTasksByProcInstIdAndChildTaskTagOrderByCreateTimeDesc(execution.procInstId,false);
        }else{
            Set<String> taskIdFilter = new HashSet<String>();
            List<Task> preTakss = arg.taskDao.findTasksByProcInstIdAndChildTaskTagOrderByCreateTimeDesc(execution.procInstId,false);
            for(Task preTask : preTakss){
                if(isBlank(preTask.getParentTaskId())){
                    finalTasks.add(preTask);
                    continue;
                }
                if(taskIdFilter.contains(preTask.getId())){
                    continue;
                }
                Task parentTask = arg.taskDao.findOne(preTask.getParentTaskId());
                finalTasks.add(parentTask);
                taskIdFilter.add(parentTask.getId());
            }
        }
        List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
        for(Task resultTask : finalTasks){
            // TODO fetch variables for proc and task (cache)
            Map<String,Object> resultMap = new HashMap<String,Object>();
            Flow flowInst = super.getFlowComplete(arg.definationService,resultTask.getFlowId());
            resultMap.put("taskId",resultTask.getId());
            resultMap.put("flowId",resultTask.getFlowId());
            resultMap.put("flowName",flowInst.getFlowName());
            resultMap.put("assignUser",resultTask.getAssignUser());
            resultMap.put("procInstId",resultTask.getProcInstId());
            resultMap.put("nodeId",resultTask.getNodeId());
            resultMap.put("nodeName",flowInst.getNodeIdMap().get(resultTask.getNodeId()).getName());
            resultMap.put("nodeDefId",resultTask.getNodeDefId());
            resultMap.put("bussId",resultTask.getBussId());
            resultMap.put("bussTable",resultTask.getBussTable());
            resultMap.put("formKey",flowInst.getFormKey());
            resultMap.put("description",resultTask.getDescription());
            resultMap.put("startTime",resultTask.getCreateTime());
            resultMap.put("endTime",resultTask.getFinishTime());
            resultList.add(resultMap);
        }
        task.setExecutedResult(resultList);
        return "success";
    }

    @Override
    public List<Map<String,Object>> handleCallback(EngineTask task) throws EngineRuntimeException {
        return (List<Map<String,Object>>)task.getExecutedResult();
    }


}

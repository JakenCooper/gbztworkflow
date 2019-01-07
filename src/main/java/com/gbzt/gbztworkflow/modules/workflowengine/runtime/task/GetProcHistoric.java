package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import org.apache.log4j.Logger;

import java.util.*;

/**
 *  proc-history: 流转信息，该接口与histproc表没有任何关系！该接口查询的是task表
 * */
public class GetProcHistoric extends EngineBaseExecutor {

    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_GETPROCHISTORIC_SYNC;

    private Logger logger = Logger.getLogger(GetProcHistoric.class);
    private static String LOGGER_TYPE_PREFIX = "GetProcHistoric,";

    public static class GetProcHistoricArg  extends EngineBaseArg implements IEngineArg{
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
        // [logic] 默认情况走上面的if条件，也就是查询所有去除子任务对应父任务的任务
        if(execution.childTaskTag == null || !execution.childTaskTag){
            // historic runtime infos for proc.
            if(!AppConst.REDIS_SWITCH) {
                finalTasks = arg.taskDao.findTasksByProcInstIdAndChildTaskTagOrderByCreateTimeMillsDesc(execution.procInstId, false);
            }else{
                finalTasks = arg.jedisService.findTaskByProcInstIdAndChildTag(execution.procInstId,false);
            }
        }else{
            System.out.println("=============================================== unknown");
            // TODO 逻辑看不懂了
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
            Map<String,Object> resultMap = new HashMap<String,Object>();
            Flow flowInst = super.getFlowComplete(arg.definationService,arg.definationCacheService,resultTask.getFlowId());
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
            resultMap.put("retreatTag",resultTask.getRetreatTag() == null?false:resultTask.getRetreatTag());
            resultMap.put("withdrawTag",resultTask.getWithdrawTag() == null ? false:resultTask.getWithdrawTag());
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

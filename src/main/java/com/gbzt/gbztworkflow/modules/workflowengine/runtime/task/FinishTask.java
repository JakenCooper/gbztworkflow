package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.flowruntime.model.TaskModel;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.ProcInstDao;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.TaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineManager;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineTaskTemplateFactory;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.utils.SimpleCache;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class FinishTask extends EngineBaseExecutor {
    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_FINISHTASK_SYNC;

    private Logger logger = Logger.getLogger(FinishTask.class);
    private static String LOGGER_TYPE_PREFIX = "FinishTask,";

    public static class FinishTaskArg implements IEngineArg{
        private String[] requiredArg = new String[]{};
        public DefinationService definationService;
        public TaskExecution execution;
        public ProcInstDao procInstDao;
        public TaskDao taskDao;
    }

    @Override
    public EngineTask generateDefaultEngineTask(IEngineArg iarg, Object externalArg) {
        FinishTask.FinishTaskArg arg = (FinishTask.FinishTaskArg) iarg;
        EngineTask engineTask = super.generateDefaultEngineTask(TASK_TYPE,arg);
        return engineTask;
    }

    @Override
    public void preHandleTask(EngineTask task) throws EngineAccessException {
        super.preHandleTask(task);
    }

    @Override
    public String executeEngineTask(EngineTask task) throws EngineRuntimeException {
        FinishTask.FinishTaskArg arg = (FinishTask.FinishTaskArg) task.getArgs();
        TaskExecution execution = arg.execution;
        String taskId = execution.taskId;   // -
        String passStr = execution.passStr; //-

        Task taskObj = arg.taskDao.findOne(taskId);
        List<Task> subTasks = arg.taskDao.findTasksByParentTaskId(taskId);

        // for next task creation
        Line nextLine = null;
        String nextNodeId = null;
        if(isNotBlank(passStr)){
            Flow flowInst = (Flow) SimpleCache.getFromCache(SimpleCache.CACHE_KEY_PREFIX_FLOW_DETAIL
                +taskObj.getFlowId());
            Node thisNode = flowInst.getNodeIdMap().get(taskObj.getNodeId());
            String nextNodeDefId = "audit-"+passStr;
            Node nextNode = null;
            for(Node node : thisNode.getNextNodes()){
                if(node.getNodeDefId().equals(nextNodeDefId)){
                    nextNode = node;
                }
            }
            if(nextNode!=null){
                nextNodeId = nextNode.getId();
                nextLine = flowInst.getLineMap().get(thisNode.getId()+","+nextNode.getId());
            }
        }

        if(isBlank(taskObj.getParentTaskId())){
            if(isBlank(subTasks)){
                updateTask(taskObj,arg.taskDao,execution);

                TaskExecution nextExcution = new TaskExecution();
                nextExcution.flowId  = taskObj.getFlowId();
                nextExcution.passUser = execution.passUser;
                nextExcution.passStr = execution.passStr;
                nextExcution.procInstId = taskObj.getProcInstId();
                nextExcution.nodeId = nextNodeId;
                nextExcution.assignUser = execution.assignUser;
                nextExcution.assignUserList = execution.assignUserList;

                CreateTask.CreateTaskArg nextArg = new CreateTask.CreateTaskArg();
                nextArg.execution = nextExcution;
                nextArg.definationService = arg.definationService;
                nextArg.taskDao = arg.taskDao;
                nextArg.lineInst = nextLine;

                EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(CreateTask.class,arg,null);
                try {
                    String result = EngineManager.execute(engineTask);
                    task.setExecutedResult(result);
                } catch (Exception e) {
                    throw e;
                }
            }else{
                for(Task subTask : subTasks){
                    if(!subTask.isFinishTag()){
                        throw new EngineRuntimeException("at least one sub task not finished..");
                    }
                }
            }
        }else{
            //TODO consider finishType -
            updateTask(taskObj,arg.taskDao,execution);
            List<Task> finalSubTasks = arg.taskDao.findTasksByParentTaskId(taskId);
            boolean allFinished = true;
            for(Task finalTask : finalSubTasks){
                if(!finalTask.isFinishTag()){
                    allFinished = false;
                    break;
                }
            }
            if(allFinished){

            }
        }
        return null;
    }

    private void updateTask(Task taskObj,TaskDao taskDao,TaskExecution execution){
        taskObj.setFinishTag(true);
        taskObj.setFinishUser(execution.passUser); // -
        taskObj.setFinishTime(new Date());
        taskObj.setDuration(new Date().getTime()-taskObj.getCreateTime().getTime());
        taskObj.setDescription(execution.description);
        taskDao.save(taskObj);
    }

    @Override
    public String handleCallback(EngineTask task) throws EngineRuntimeException {
        return "success";
    }




}

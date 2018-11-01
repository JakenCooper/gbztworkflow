package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.flowruntime.model.TaskModel;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.*;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.HistTask;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskVariables;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineManager;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineTaskTemplateFactory;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import com.gbzt.gbztworkflow.utils.SimpleCache;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
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
        public TaskVariableDao taskVariableDao;
        public HistTaskDao histTaskDao;
        public HistProcDao histProcDao;

        private Map<String,String> argMap;
        private Flow flowInst;
    }

    @Override
    public EngineTask generateDefaultEngineTask(IEngineArg iarg, Object externalArg) {
        FinishTask.FinishTaskArg arg = (FinishTask.FinishTaskArg) iarg;
        EngineTask engineTask = super.generateDefaultEngineTask(TASK_TYPE,arg);
        // function repeatable.
        ((FinishTaskArg)engineTask.getArgs()).argMap = (Map<String,String>) externalArg;
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
        Node thisNode = arg.definationService.getNodeByIdSimple(taskObj.getNodeId());
        Flow flowInst = super.getFlowComplete(arg.definationService,taskObj.getFlowId());
        arg.flowInst = flowInst;
        // only parent task can move on to next task
        if(isNotBlank(passStr) && isBlank(taskObj.getParentTaskId())){
            Object[] resultArr = super.getNextNodeInfo(arg.definationService,execution.flowId,
                    taskObj.getNodeId(),passStr);
            nextNodeId = ((Node)resultArr[0]).getId();
            nextLine = (Line)resultArr[1];
        }

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
        nextArg.procInstDao =  arg.procInstDao;
        nextArg.lineInst = nextLine;
        nextArg.histProcDao = arg.histProcDao;

        if(isBlank(taskObj.getParentTaskId())){
            if(isBlank(subTasks)){
                updateTask(taskObj,arg.taskDao,execution);
                addVariables(arg);
                addHistTask(taskObj,arg.histTaskDao,execution);
                EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(CreateTask.class,nextArg,null);
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
                if(isBlank(thisNode.getAssignUser())){
                    throw new EngineRuntimeException("solid assign user not found");
                }
                if(thisNode.getNextNodes() == null || thisNode.getNextNodes().size() == 0){
                    throw new EngineRuntimeException("solid step not found.");
                }
                execution.passUser = taskObj.getAssignUser();
                updateTask(taskObj,arg.taskDao,execution);
                addHistTask(taskObj,arg.histTaskDao,execution);
                nextNodeId = thisNode.getNextNodes().get(0).getId();
                nextLine = flowInst.getLineMap().get(thisNode.getId()+","+nextNodeId);
                nextArg.execution.nodeId = nextNodeId;
                nextArg.lineInst = nextLine;
                nextArg.execution.assignUser = thisNode.getAssignUser();
                nextArg.execution.assignUserList = null;

                EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(CreateTask.class,nextArg,null);
                try {
                    String result = EngineManager.execute(engineTask);
                    task.setExecutedResult(result);
                } catch (Exception e) {
                    throw e;
                }
            }
        }else{
            //TODO consider finishType -
            updateTask(taskObj,arg.taskDao,execution);
            addVariables(arg);
            addHistTask(taskObj,arg.histTaskDao,execution);
            List<Task> finalSubTasks = arg.taskDao.findTasksByParentTaskId(taskId);
            boolean allFinished = true;
            for(Task finalTask : finalSubTasks){
                if(!finalTask.isFinishTag()){
                    allFinished = false;
                    break;
                }
            }
            if(allFinished){
                Task parentTask = arg.taskDao.findOne(taskObj.getParentTaskId());
                execution.taskId = parentTask.getId();
                FinishTask.FinishTaskArg parentArg = new FinishTask.FinishTaskArg();
                parentArg.execution = execution;
                parentArg.definationService = arg.definationService;
                parentArg.taskDao = arg.taskDao;
                parentArg.procInstDao = arg.procInstDao;
                EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(FinishTask.class,parentArg,null);
                try {
                    String result = EngineManager.execute(engineTask);
                    task.setExecutedResult(result);
                } catch (Exception e) {
                    throw e;
                }
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

    private void addHistTask(Task taskObj,HistTaskDao histTaskDao,TaskExecution execution){
        histTaskDao.deleteHistTaskByProcInstIdAndUserId(taskObj.getProcInstId(),execution.passUser);
        HistTask histTask = new HistTask();
        histTask.genBaseVariables();;
        histTask.setId(CommonUtils.genUUid());
        histTask.setTaskId(taskObj.getId());
        histTask.setProcInstId(taskObj.getProcInstId());
        histTask.setUserId(execution.passUser);
        histTaskDao.save(histTask);
    }

    private void addVariables(FinishTask.FinishTaskArg arg){
        TaskExecution execution = arg.execution;
        if(execution.getArgMap() != null  && execution.getArgMap().keySet().size() > 0){
            Map<String,String> argMap = execution.getArgMap();
            List<TaskVariables> variables = new ArrayList<TaskVariables>();
            for(String argKey : argMap.keySet()) {
                String argValue = argMap.get(argKey);
                String realKey = null;
                String varType = null;
                if (argKey.startsWith(TaskVariables.VARS_TYPE_PROC)) {
                    realKey = argKey.substring(argKey.indexOf(TaskVariables.VARS_TYPE_PROC) + 5, argKey.length());
                    varType = "proc";
                } else if (argKey.startsWith(TaskVariables.VARS_TYPE_TASK)) {
                    realKey = argKey.substring(argKey.indexOf(TaskVariables.VARS_TYPE_TASK) + 5, argKey.length());
                    varType = "task";
                }else{
                    continue;
                }
                TaskVariables taskVariables = new TaskVariables();
                taskVariables.setId(CommonUtils.genUUid());
                taskVariables.setNodeId(execution.nodeId);
                taskVariables.setProcInstId(execution.procInstId);
                taskVariables.setTaskId(execution.taskId);
                taskVariables.setKey(realKey);
                taskVariables.setValue(argValue);
                taskVariables.setType(varType);
                variables.add(taskVariables);
            }
            arg.taskVariableDao.save(variables);
        }
    }

    @Override
    public String handleCallback(EngineTask task) throws EngineRuntimeException {
        return "success";
    }




}

package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.TaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.ProcInst;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateTask extends EngineBaseExecutor {
    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_CREATETASK_SYNC;

    private Logger logger = Logger.getLogger(CreateTask.class);
    private static String LOGGER_TYPE_PREFIX = "CreateTask,";

    public static class CreateTaskArg implements IEngineArg {
        private String[] requiredArg = new String[]{"flowId","procInstId","nodeDefId","assignUser","passUser"};
        public DefinationService definationService;
        public TaskDao taskDao;
        public TaskExecution execution;
        public Line lineInst;

        private String flowId;
        private String nodeDefId;
        private ProcInst procInst;
    }

    @Override
    public EngineTask generateDefaultEngineTask(IEngineArg iarg, Object externalArg) {
        CreateTask.CreateTaskArg arg = (CreateTask.CreateTaskArg)iarg;
        EngineTask engineTask = super.generateDefaultEngineTask(TASK_TYPE,arg);
        return engineTask;
    }

    @Override
    public void preHandleTask(EngineTask task) throws EngineAccessException {
        CreateTask.CreateTaskArg arg = (CreateTask.CreateTaskArg)task.getArgs();
        TaskExecution execution = arg.execution;
        if(isBlank(execution.flowId) || isBlank(execution.procInstId) || arg.lineInst == null){
            throw new EngineAccessException("not enough arguments");
        }
        if(isBlank(execution.nodeDefId) && isBlank(execution.nodeId)){
            throw new EngineAccessException("no node arguments.");
        }
        if(isBlank(execution.assignUser) && isBlank(execution.assignUserList)){
            throw new EngineAccessException("no assign user.");
        }
    }

    @Override
    public String executeEngineTask(EngineTask task) throws EngineRuntimeException {
        String loggerType=LOGGER_TYPE_PREFIX+"executeEngineTask";
        CreateTask.CreateTaskArg arg = (CreateTask.CreateTaskArg)task.getArgs();
        TaskExecution execution = arg.execution;
        Task taskObj = new Task();
        List<Task> subTasks = new ArrayList<Task>();
        taskObj.genBaseVariables();
        taskObj.setCreateUser(execution.passUser);
        taskObj.setProcInstId(execution.procInstId);
        taskObj.setFlowId(execution.flowId);
        if(isBlank(execution.nodeId) || isBlank(execution.nodeDefId)){
            Flow flowInst = super.getFlowComplete(arg.definationService,execution.flowId);
            if(isBlank(execution.nodeId)){
                for(Node node : flowInst.getAllNodes()){
                    if(node.getNodeDefId().equals(execution.nodeDefId)){
                        execution.nodeId = node.getId();
                    }
                }
            }else{
                execution.nodeDefId = flowInst.getNodeIdMap().get(execution.nodeId).getNodeDefId();
            }
        }
        taskObj.setNodeId(execution.nodeId);
        taskObj.setNodeDefId(execution.nodeDefId);
        taskObj.setPassStr(execution.nodeDefId.split("-")[1]);

        Line lineInst = arg.lineInst;
        taskObj.setFinishType(lineInst.getFinishType());
        taskObj.setTaskType(lineInst.getTaskType());

        taskObj.setExecutionType(execution.executionType);
        taskObj.setExecutionOrder(execution.executionOrder);

        if(execution.assignUser != null){
            taskObj.setAssignUser(execution.assignUser);
            taskObj.setAssignTime(new Date());
            taskObj.setOwner(execution.assignUser);
        }else{
            for(String subUser : execution.assignUserList){
                Task subTask = new Task();
                BeanUtils.copyProperties(subTask,taskObj);
                subTask.setId(CommonUtils.genUUid());
                subTask.setAssignUser(subUser);
                subTask.setOwner(subUser);
                subTask.setParentTaskId(taskObj.getId());
                subTask.setAssignTime(new Date());
                subTask.setFinishType(AppConst.FLOWRUNTIME_FINISHTYPE_SINGLE);
                subTask.setTaskType(AppConst.FLOWRUNTIME_TASKTYPE_USER);
                subTask.setExecutionType(null);
                subTask.setExecutionOrder(null);
                subTasks.add(subTask);
            }
        }
        arg.taskDao.save(taskObj);
        if(subTasks.size()>0){
            arg.taskDao.save(subTasks);
        }
        task.setExecutedResult(taskObj.getId());
        return null;
    }

    @Override
    public String handleCallback(EngineTask task) throws EngineRuntimeException {
        return (String)task.getExecutedResult();
    }




}

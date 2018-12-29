package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.redis.service.JedisService;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.HistTaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.TaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.HistTask;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskVariables;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineManager;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineTaskTemplateFactory;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FinishTask extends EngineBaseExecutor {
    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_FINISHTASK_SYNC;

    private Logger logger = Logger.getLogger(FinishTask.class);
    private static String LOGGER_TYPE_PREFIX = "FinishTask,";

    public static class FinishTaskArg extends EngineBaseArg implements IEngineArg{
        private String[] requiredArg = new String[]{};
        public TaskExecution execution;

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

        Task taskObj = null;
        List<Task> subTasks = null;
        Node thisNode = null;
        if(!AppConst.REDIS_SWITCH){
            taskObj = arg.taskDao.findOne(taskId);
            subTasks = arg.taskDao.findTasksByParentTaskId(taskId);
            thisNode = arg.definationService.getNodeByIdSimple(taskObj.getNodeId());
        }else{
            taskObj = arg.jedisService.findTaskById(taskId);
            subTasks = arg.jedisService.findSubTaskByTaskId(taskObj.getProcInstId(),taskObj.getId());
            thisNode = arg.jedisService.findNodeById(taskObj.getNodeId());
        }

        // for next task creation
        Line nextLine = null;
        String nextNodeId = null;

        //zhangys
        Flow flowInst = super.getFlowComplete(arg.definationService,arg.definationCacheService,taskObj.getFlowId());
        arg.flowInst = flowInst;
        // only parent task can move on to next task
        if(isNotBlank(passStr) && isBlank(taskObj.getParentTaskId())){
            Object[] resultArr = super.getNextNodeInfo(arg.definationService,arg.definationCacheService,execution.flowId,
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
        nextArg.lineInst = nextLine;
        nextArg.copyFrom(arg);

        if(isBlank(taskObj.getParentTaskId())){
            // modify by zhangys @20181130 对于父任务优先使用soliduser作为下一步分配用户
            // 注意：soliduser是 “下一个节点的soliduser”
            String solidUser = null;
            if(isNotBlank(nextNodeId)){
                Node nextNode = null;
                if(!AppConst.REDIS_SWITCH) {
                    nextNode = arg.definationService.getNodeByIdSimple(nextNodeId);
                }else{
                    nextNode = arg.jedisService.findNodeById(nextNodeId);
                }
                solidUser = nextNode.getAssignUser();
            }
            if(isBlank(subTasks)){
                updateTask(taskObj,arg.taskDao,arg.jedisService,execution);
                addVariables(arg);
                addHistTask(taskObj,arg.histTaskDao,arg.jedisService,execution);
                if(isNotBlank(solidUser)){
                    nextArg.execution.assignUser = solidUser;
                }
                EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(CreateTask.class,nextArg,null);
                try {
                    String result = EngineManager.execute(engineTask);
                    task.setExecutedResult(result);
                } catch (Exception e) {
                    throw e;
                }
            }else{

                /*************************    多实例任务现有的重要逻辑： ***********************/
                // 1.多实例任务最终的完成者是多实例任务的创建者
                // 2.多实例任务会在所有子任务都完成之后自动完成，但是只能完成多实例任务下一步能到达所有节点的第一个节点
                // 3.多实例任务的下一步任务指派人优先使用下一步节点的固定用户(soliduser)，然而如果没有指定固定用户，则会使用多实例任务的完成者作为下一步节点的任务分配者
                // 所以，如果没有指定soliduser，多实例任务上一步任务的完成者、多实例任务的完成者(此处通过passUser实现)、多实例任务下一步任务的指派者都是一个人
                /****************************************************************************************/

                for(Task subTask : subTasks){
                    if(!subTask.getFinishTag()){
                        throw new EngineRuntimeException("at least one sub task not finished..");
                    }
                }
                if(thisNode.getNextNodes() == null || thisNode.getNextNodes().size() == 0){
                    throw new EngineRuntimeException("solid step not found.");
                }
                // modify by zhangys @20181130 优先使用soliduser，如果没有时使用当前task的createUser
                if(isNotBlank(solidUser)){
                    execution.passUser = solidUser;
                }
                updateTask(taskObj,arg.taskDao,arg.jedisService,execution);
                addHistTask(taskObj,arg.histTaskDao,arg.jedisService,execution);
                //默认选择第一个step，所以如果送阅任务可以到多个下一步节点，只有第一个会生效
                nextNodeId = thisNode.getNextNodes().get(0).getId();
                nextLine = flowInst.getLineMap().get(thisNode.getId()+","+nextNodeId);
                if(nextLine == null){
                    throw new RuntimeException("no detected next line when finishing parent-sub tasks");
                }
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
            updateTask(taskObj,arg.taskDao,arg.jedisService,execution);
            addVariables(arg);
            addHistTask(taskObj,arg.histTaskDao,arg.jedisService,execution);
            // [logic] 每次完成子任务时，检测是否所有子任务都已经完成，如果所有子任务都已经完成，则将父任务标记为完成，
            //  顺便完成其他逻辑处理，例如创建histproc
            List<Task> finalSubTasks = null;
            if(!AppConst.REDIS_SWITCH){
                finalSubTasks = arg.taskDao.findTasksByParentTaskId(taskId);
            }else{
                finalSubTasks = arg.jedisService.findSubTaskByTaskId(taskObj.getProcInstId(),taskObj.getId());
            }
            boolean allFinished = true;
            for(Task finalTask : finalSubTasks){
                if(!finalTask.getFinishTag()){
                    allFinished = false;
                    break;
                }
            }
            if(allFinished){
                Task parentTask = null;
                if(AppConst.REDIS_SWITCH){
                    parentTask = arg.taskDao.findOne(taskObj.getParentTaskId());
                }else{
                    parentTask = arg.jedisService.findTaskById(taskObj.getParentTaskId());
                }
                execution.taskId = parentTask.getId();
                // [logic] 对创建多实例下一步任务的必要属性进行设置 -- begin
                execution.passUser = parentTask.getCreateUser();
                Node parentNextNode = thisNode.getNextNodes().get(0);
                String parentNextPassStr = (parentNextNode.getNodeDefId().split("-"))[1];
                execution.passStr = parentNextPassStr;
                // 对创建多实例下一步任务的必要属性进行设置 -- end
                FinishTask.FinishTaskArg parentArg = new FinishTask.FinishTaskArg();
                parentArg.execution = execution;
                parentArg.copyFrom(arg);

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

    private void updateTask(Task taskObj, TaskDao taskDao, JedisService jedisService,TaskExecution execution){
        taskObj.setFinishTag(true);
        taskObj.setFinishUser(execution.passUser); // -
        taskObj.setFinishTime(new Date());
        taskObj.setDuration(new Date().getTime()-taskObj.getCreateTime().getTime());
        taskObj.setDescription(execution.description);
        if(!AppConst.REDIS_SWITCH) {
            taskDao.save(taskObj);
        }else{
            jedisService.updateTask(taskObj);
            jedisService.delUndoWhenFinishTask(execution.passUser,taskObj.getProcInstId(),taskObj.getId());
        }
    }

    private void addHistTask(Task taskObj,HistTaskDao histTaskDao,JedisService jedisService,TaskExecution execution){
        HistTask histTask = new HistTask();
        histTask.genBaseVariables();
        histTask.setId(CommonUtils.genUUid());
        histTask.setTaskId(taskObj.getId());
        histTask.setProcInstId(taskObj.getProcInstId());
        histTask.setUserId(execution.passUser);
        if(!AppConst.REDIS_SWITCH) {
            histTaskDao.deleteHistTaskByProcInstIdAndUserId(taskObj.getProcInstId(), execution.passUser);
            histTaskDao.save(histTask);
        }else{
            jedisService.delHistTaskByUserIdAndProcInstId(histTask.getUserId(),histTask.getProcInstId());
            jedisService.saveHistTaskOrUndo(histTask,null);
        }
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
                if (argKey.startsWith(TaskVariables.VARS_TYPE_PROC_PREFIX)) {
                    realKey = argKey.substring(argKey.indexOf(TaskVariables.VARS_TYPE_PROC_PREFIX) + 5, argKey.length());
                    varType = TaskVariables.VARS_TYPE_PROC;
                } else if (argKey.startsWith(TaskVariables.VARS_TYPE_TASK_PREFIX)) {
                    realKey = argKey.substring(argKey.indexOf(TaskVariables.VARS_TYPE_TASK_PREFIX) + 5, argKey.length());
                    varType = TaskVariables.VARS_TYPE_TASK;
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
                taskVariables.genBaseVariables();
                // 变量的创建人是当前任务的提交人
                taskVariables.setCreateUser(execution.passUser);
                variables.add(taskVariables);
            }
            if(!AppConst.REDIS_SWITCH) {
                arg.taskVariableDao.save(variables);
            }else{
                arg.jedisService.saveTaskVariables(variables);
            }
        }
    }

    @Override
    public String handleCallback(EngineTask task) throws EngineRuntimeException {
        return "success";
    }




}

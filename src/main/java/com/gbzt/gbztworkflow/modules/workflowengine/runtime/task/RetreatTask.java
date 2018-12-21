package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.HistTaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.*;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineManager;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineTaskTemplateFactory;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import com.gbzt.gbztworkflow.utils.LogUtils;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

public class RetreatTask extends EngineBaseExecutor {

    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_RETREATTASK_SYNC;

    private Logger logger = Logger.getLogger(RetreatTask.class);
    private static String LOGGER_TYPE_PREFIX = "RetreatTask,";

    private static final String OPER_TYPE_RETREAT = "retreat";
    private static final String OPER_TYPE_WITHDRAW = "withdraw";

    public static class RetreatTaskArg extends EngineBaseArg implements IEngineArg{
        public TaskExecution execution;

        private HistProc lastHistProc;
        private HistProc secondLastHistProc;
        private Flow flow;
        private Line lineInst;
        private Task lastTaskObj ;
        private Task secondLastTaskObj ;
        private Task thisTaskObj ;
        private boolean multiInstanceTag = false;
    }

    @Override
    public EngineTask generateDefaultEngineTask(IEngineArg iarg, Object externalArg) {
        RetreatTask.RetreatTaskArg arg = (RetreatTask.RetreatTaskArg)iarg;
        EngineTask task = super.generateDefaultEngineTask(TASK_TYPE,arg);
        return task;
    }

    @Override
    public void preHandleTask(EngineTask task) throws EngineAccessException {
        RetreatTask.RetreatTaskArg arg = (RetreatTask.RetreatTaskArg)task.getArgs();
        TaskExecution execution = arg.execution;
        if(isBlank(execution.taskId) || isBlank(execution.procInstId) || isBlank(execution.passUser)){
            throw new EngineAccessException("basic arguments not enough.");
        }
        if(isBlank(execution.retreatOperType)){
            throw new EngineAccessException("retreat oper type is null.");
        }
        if(!execution.retreatOperType.equals(OPER_TYPE_RETREAT) && !execution.retreatOperType.equals(OPER_TYPE_WITHDRAW)){
            throw new EngineAccessException("retreat oper type wrongly setted.");
        }
    }

    @Override
    public String executeEngineTask(EngineTask task) throws EngineRuntimeException {
        RetreatTask.RetreatTaskArg arg = (RetreatTask.RetreatTaskArg)task.getArgs();
        TaskExecution execution = arg.execution;

        //detect oper or real submit.
        if(!execution.isRetreatSubmitTag()){
            task.setExecutedResult(canWithdrawOrRetreat(arg));
            return "success";
        }else{
            boolean canWithdrawOrRetreat = canWithdrawOrRetreat(arg);
            if(!canWithdrawOrRetreat){
                task.setExecutedResult("fail");
                return "success";
            }
            String procInstId = arg.lastHistProc.getProcInstId();
            String operUser = execution.passUser;
            Task secondLastTask = arg.secondLastTaskObj;
            Task lastTask = arg.lastTaskObj;
            Task thisTask = arg.thisTaskObj;

            // [logic] 收回或者退回操作，更新最后一个任务为完成状态，以及更新相关字段
            if(OPER_TYPE_WITHDRAW.equals(execution.retreatOperType)){
                if(!AppConst.REDIS_SWITCH) {
                    arg.taskDao.withdrawAllfinishedTaskByProcInstIdAndTaskId(procInstId, new Date(), operUser, lastTask.getId());
                }else{
                    arg.jedisService.withdrawAllfinishedTaskByProcInstIdAndTaskId(procInstId, new Date(), operUser, lastTask.getId());
                }
                //  [logic][多实例]  对于多实例任务的收回操作，采用暴力的方式：
                // 1. 将多实例子任务和父任务全部删除
                // 2.将提交多实例用户的最后一个任务更新成已完成，以及收回标记设置为true
                // 3.删除最新一条histproc，也同时删除taskproc（因为会导致已办数据查询不准确），然后再创建任务——因为taskid不同所以必须这样处理
                if(arg.multiInstanceTag){
                    List<Task> subTasks = arg.taskDao.findTasksByParentTaskId(lastTask.getId());
                    if(!AppConst.REDIS_SWITCH) {
                        arg.taskDao.delete(subTasks);
                        arg.taskDao.delete(lastTask);
                    }else{
                        arg.jedisService.delTaskByTaskIdAndSubTaskIds(lastTask,subTasks);
                    }
                    secondLastTask.setFinishTag(true);
                    secondLastTask.setFinishUser(secondLastTask.getAssignUser());
                    secondLastTask.setWithdrawTag(true);
                    secondLastTask.setWithdrawDescription("收回");
                    if(!AppConst.REDIS_SWITCH) {
                        arg.taskDao.save(secondLastTask);
                        arg.histProcDao.delete(arg.lastHistProc);
                    }else{
                        arg.jedisService.updateTask(secondLastTask);
                        arg.jedisService.delHistProc(arg.lastHistProc);
                    }
                    createTaskAfterAll(arg,secondLastTask);
                    task.setExecutedResult("success");
                    return "success";
                }
            }else{
                if(!AppConst.REDIS_SWITCH) {
                    arg.taskDao.retreatUnFinishTaskByProcInstIdAndTaskId(procInstId, new Date(), operUser, lastTask.getId());
                }else{
                    arg.jedisService.retreatUnFinishTaskByProcInstIdAndTaskId(procInstId, new Date(), operUser, lastTask.getId());
                }
            }

            // [logic] 在退回操作时创建最后一个任务的已办事务，对于收回操作因为已经在FinishTask的时候创建过histtask，所以此处不用创建
            if(OPER_TYPE_RETREAT.equals(execution.retreatOperType)){
                addHistTask(lastTask,arg.histTaskDao,lastTask.getAssignUser());
            }
            createTaskAfterAll(arg,secondLastTask);

            if(!AppConst.REDIS_SWITCH) {
                arg.histProcDao.delete(arg.lastHistProc);
            }else{
                arg.jedisService.delHistProc(arg.lastHistProc);
            }
            task.setExecutedResult("success");
        }
        return "success";
    }


    private void createTaskAfterAll(RetreatTask.RetreatTaskArg arg,Task targetTask){
        TaskExecution nextExcution = new TaskExecution();
        nextExcution.flowId  = targetTask.getFlowId();
        nextExcution.passUser = targetTask.getFinishUser();
        nextExcution.passStr = targetTask.getPassStr();
        nextExcution.procInstId = targetTask.getProcInstId();
        nextExcution.nodeId = targetTask.getNodeId();
        nextExcution.assignUser = targetTask.getAssignUser();
        nextExcution.assignUserList = targetTask.getAssignUserList();
        //  [logic] 对于收回或者退回的情况，已经删除最后一条histproc，创建任务也无需创建多余的histproc
        nextExcution.needHistProc = false;


        CreateTask.CreateTaskArg nextArg = new CreateTask.CreateTaskArg();
        nextArg.execution = nextExcution;
        nextArg.lineInst = arg.lineInst;
        nextArg.copyFrom(arg);

        EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(CreateTask.class,nextArg,null);
        try {
            EngineManager.execute(engineTask);
        } catch (Exception e) {
            throw e;
        }
    }

    // 针对于退回的情况，必须手动添加histtask，否则在退回人的已办列表中找不到任务数据（收回因为在FinishTask任务时已经创建histtask，所以此处不做处理）
    private void addHistTask(Task taskObj,HistTaskDao histTaskDao,String user){
        histTaskDao.deleteHistTaskByProcInstIdAndUserId(taskObj.getProcInstId(),user);
        HistTask histTask = new HistTask();
        histTask.genBaseVariables();
        histTask.setId(CommonUtils.genUUid());
        histTask.setTaskId(taskObj.getId());
        histTask.setProcInstId(taskObj.getProcInstId());
        histTask.setUserId(user);
        histTaskDao.save(histTask);
    }

    private boolean canWithdrawOrRetreat(RetreatTask.RetreatTaskArg arg){
        String loggerType = LOGGER_TYPE_PREFIX+"canWithdrawOrRetreat";
        TaskExecution execution = arg.execution;
        List<HistProc> histProcs = null;
        if(!AppConst.REDIS_SWITCH) {
            histProcs = arg.histProcDao.findHistProcsByProcInstIdOrderByCreateTimeMillsAsc(execution.procInstId);
        }else{
            histProcs = arg.jedisService.findHistProcByProcInstId(execution.procInstId);
        }
        // 如果当前流程的 prochist 数量不足2，就完全没有收回或者退回的可能
        if(histProcs == null || histProcs.size() < 2){
            logger.warn(LogUtils.getMessage(loggerType,"Proc【"+execution.procInstId+"】find no histproc or histproc size lt 2"));
            return false;
        }
        ProcInst procInst = null;
        if(!AppConst.REDIS_SWITCH) {
            procInst = arg.procInstDao.findOne(execution.procInstId);
        }else{
            procInst = arg.jedisService.findProcInstById(execution.procInstId);
        }
        HistProc lastHistProc = histProcs.get(histProcs.size()-1);
        HistProc secondLastHistProc = histProcs.get(histProcs.size()-2);

        //zhangys
        Flow flowInst = super.getFlowComplete(arg.definationService,arg.definationCacheService,procInst.getFlowId());
        Node secondLastNode = flowInst.getNodeIdMap().get(secondLastHistProc.getNodeId());
        Node lastNode  = flowInst.getNodeIdMap().get(lastHistProc.getNodeId());
        Line line = flowInst.getLineMap().get(secondLastNode.getId()+","+lastNode.getId());
        arg.lastHistProc = lastHistProc;
        arg.secondLastHistProc = secondLastHistProc;
        arg.flow = flowInst;
        arg.lineInst = line;
        if(line == null){
            logger.warn(LogUtils.getMessage(loggerType,"Proc【"+execution.procInstId+",withdraw: 】can not detect related line."));
            return false;
        }
        Task lastTaskObj = null;
        Task secondLastTaskObj =null;
        Task thisTaskObj = null;
        if(!AppConst.REDIS_SWITCH) {
            lastTaskObj = arg.taskDao.findOne(lastHistProc.getTaskId());
            secondLastTaskObj = arg.taskDao.findOne(secondLastHistProc.getTaskId());
            thisTaskObj = arg.taskDao.findOne(execution.taskId);
        }else{
            lastTaskObj = arg.jedisService.findTaskById(lastHistProc.getTaskId());
            secondLastTaskObj = arg.jedisService.findTaskById(secondLastHistProc.getTaskId());
            thisTaskObj = arg.jedisService.findTaskById(execution.taskId);
        }

        arg.lastTaskObj = lastTaskObj;
        arg.secondLastTaskObj = secondLastTaskObj;
        arg.thisTaskObj = thisTaskObj;

        if(OPER_TYPE_WITHDRAW.equals(execution.retreatOperType)){
            // [logic][多实例] 最新任务是多实例父任务，并且其中有任何一个子任务已经完成的情况下不允许收回
            boolean childTaskFinishedTag = false;
            if(lastTaskObj.isChildTaskTag()){
                List<Task> subTasks = arg.taskDao.findTasksByParentTaskId(lastHistProc.getTaskId());
                for(Task subTask : subTasks){
                    if(subTask.isFinishTag()){
                        childTaskFinishedTag = true;
                        break;
                    }
                }
                if(childTaskFinishedTag){
                    logger.warn(LogUtils.getMessage(loggerType,"Proc【"+execution.procInstId+",withdraw: 】one of sub tasks already finished,cannot retreat."));
                    return false;
                }
            }
            //  [logic][多实例] 如果histproc的倒数第二步是多实例任务，并且已经完成，不允许收回
            if(secondLastTaskObj.isChildTaskTag() && secondLastTaskObj.isFinishTag()){
                logger.debug(LogUtils.getMessage(loggerType,"Proc【"+execution.procInstId+",retreat: 】last step is multi thread task"));
                return false;
            }

            /*if(lastTaskObj.getFinishTime() != null){
                logger.warn(LogUtils.getMessage(loggerType,"Proc【"+execution.procInstId+",withdraw: 】already finished."));
                return false;
            }*/
            if(!line.isCanWithdraw()){
                logger.warn(LogUtils.getMessage(loggerType,"Proc【"+execution.procInstId+",withdraw: 】sry,can not withdraw in line model"));
                return false;
            }

            if(execution.taskId.equals(secondLastHistProc.getTaskId()) && execution.passUser.equals(secondLastHistProc.getUserId())){
                if(lastTaskObj.isChildTaskTag()){
                    // 的确是允许的多实例收回任务（多实例子任务没有任何一个已经完成），将arg对象中的标志位设置成true，在之后真正的收回操作时需要特殊处理
                    arg.multiInstanceTag = true;
                }
                return true;
            }
            return false;
        }else if(OPER_TYPE_RETREAT.equals(execution.retreatOperType)){
            // [logic][多实例] 多实例子任务不允许退回
            if(isNotBlank(thisTaskObj.getParentTaskId())){
                logger.debug(LogUtils.getMessage(loggerType,"Proc【"+execution.procInstId+",retreat: 】multi thread task's sub task cannot retreat"));
                return false;
            }
            //  [logic][多实例] 如果histproc的倒数第二步是多实例任务，并且已经完成，不允许退回
            if(secondLastTaskObj.isChildTaskTag() && secondLastTaskObj.isFinishTag()){
                logger.debug(LogUtils.getMessage(loggerType,"Proc【"+execution.procInstId+",retreat: 】last step is multi thread task"));
                return false;
            }
            if(!line.isCanRetreat()){
                logger.warn(LogUtils.getMessage(loggerType,"Proc【"+execution.procInstId+",retreat: 】sry,can not retreat in line model"));
                return false;
            }
            if(execution.taskId.equals(lastHistProc.getTaskId()) && execution.passUser.equals(lastHistProc.getUserId())){
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public Object handleCallback(EngineTask task) throws EngineRuntimeException {
        RetreatTask.RetreatTaskArg arg = (RetreatTask.RetreatTaskArg)task.getArgs();
        if(!arg.execution.isRetreatSubmitTag()){
            return (boolean)task.getExecutedResult();
        }else{
            return (String)task.getExecutedResult();
        }
    }

}

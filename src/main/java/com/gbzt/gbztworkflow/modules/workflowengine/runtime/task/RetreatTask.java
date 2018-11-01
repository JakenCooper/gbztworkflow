package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.HistProcDao;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.HistTaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.ProcInstDao;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.TaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.HistProc;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.ProcInst;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineManager;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineTaskTemplateFactory;
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

    public static class RetreatTaskArg implements IEngineArg{
        public TaskExecution execution;
        public DefinationService definationService;
        public ProcInstDao procInstDao;
        public TaskDao taskDao;
        public HistProcDao histProcDao;

        private HistProc lastHistProc;
        private HistProc secondLastHistProc;
        private Flow flow;
        private Line lineInst;
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
            String finishTime = CommonUtils.formatDate(new Date());
            String operUser = execution.passUser;
            Flow flowInst = arg.flow;

            if(OPER_TYPE_WITHDRAW.equals(execution.retreatOperType)){
                arg.taskDao.withdrawAllUnfinishedTaskByProcInstId(procInstId,new Date(),operUser);
            }else{
                arg.taskDao.retreatAllUnfinishedTaskByProcInstId(procInstId,new Date(),operUser);
            }
            Task secondLastTask = arg.taskDao.findOne(arg.secondLastHistProc.getTaskId());
            createTaskAfterAll(arg,secondLastTask);

            arg.histProcDao.delete(arg.lastHistProc);
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

        CreateTask.CreateTaskArg nextArg = new CreateTask.CreateTaskArg();
        nextArg.execution = nextExcution;
        nextArg.definationService = arg.definationService;
        nextArg.taskDao = arg.taskDao;
        nextArg.procInstDao =  arg.procInstDao;
        nextArg.lineInst = arg.lineInst;
        nextArg.histProcDao = arg.histProcDao;

        EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(CreateTask.class,nextArg,null);
        try {
            EngineManager.execute(engineTask);
        } catch (Exception e) {
            throw e;
        }
    }

    private boolean canWithdrawOrRetreat(RetreatTask.RetreatTaskArg arg){
        String loggerType = LOGGER_TYPE_PREFIX+"canWithdrawOrRetreat";
        TaskExecution execution = arg.execution;
        List<HistProc> histProcs = arg.histProcDao.findHistProcsByProcInstIdOrderByCreateTimeMillsAsc(execution.procInstId);
        if(histProcs == null || histProcs.size() < 2){
            logger.warn(LogUtils.getMessage(loggerType,"Proc【"+execution.procInstId+"】find no histproc or histproc size lt 2"));
            return false;
        }
        ProcInst procInst = arg.procInstDao.findOne(execution.procInstId);
        HistProc lastHistProc = histProcs.get(histProcs.size()-1);
        HistProc secondLastHistProc = histProcs.get(histProcs.size()-2);

        Flow flowInst = super.getFlowComplete(arg.definationService,procInst.getFlowId());
        //ensential logic!
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

        if(OPER_TYPE_WITHDRAW.equals(execution.retreatOperType)){
            // TODO [doubt]
            if(arg.taskDao.findOne(lastHistProc.getTaskId()).getFinishTime() != null){
                logger.warn(LogUtils.getMessage(loggerType,"Proc【"+execution.procInstId+",withdraw: 】already finished."));
                return false;
            }
            if(!line.isCanWithdraw()){
                logger.warn(LogUtils.getMessage(loggerType,"Proc【"+execution.procInstId+",withdraw: 】sry,can not withdraw in line model"));
                return false;
            }

            if(execution.taskId.equals(secondLastHistProc.getTaskId()) && execution.passUser.equals(secondLastHistProc.getUserId())){
                return true;
            }
            return false;
        }else if(OPER_TYPE_RETREAT.equals(execution.retreatOperType)){
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

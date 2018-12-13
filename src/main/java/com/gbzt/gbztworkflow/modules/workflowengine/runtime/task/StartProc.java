package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.ProcInst;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineManager;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineTaskTemplateFactory;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import org.apache.log4j.Logger;

import java.util.List;

public class StartProc extends EngineBaseExecutor {

    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_STARTPROC_SYNC;

    private Logger logger = Logger.getLogger(StartProc.class);
    private static String LOGGER_TYPE_PREFIX = "StartProc,";

    public static class StartProcArg extends EngineBaseArg implements IEngineArg {
        private String[] requiredArg = new String[]{"flowId","bussId","passUser","formKey"};
        private String[] additionArg = new String[]{"passStr"};
        public TaskExecution execution;

        private Flow flowInst;
        private Line targetLine;
    }

    @Override
    public EngineTask generateDefaultEngineTask(IEngineArg iarg, Object externalArg) {
        StartProc.StartProcArg arg = (StartProc.StartProcArg)iarg;
        EngineTask engineTask = super.generateDefaultEngineTask(TASK_TYPE,arg);
        return engineTask;
    }

    @Override
    public void preHandleTask(EngineTask task) throws EngineAccessException {
        StartProc.StartProcArg arg = (StartProc.StartProcArg)task.getArgs();
        TaskExecution execution = arg.execution;
        if(isBlank(execution.flowId) || isBlank(execution.bussId) || isBlank(execution.passUser)){
            throw new EngineAccessException("arguments not enough..");
        }
        if(isNotBlank(execution.passStr)){
            if(isBlank(execution.assignUser) && isBlank(execution.assignUserList)){
                throw new EngineAccessException("no assigned user(s)..");
            }
            Flow flowInst = getFlowComplete(arg.definationService,execution.flowId);
            List<Node> nextNodes = flowInst.getStartNode().getNextNodes();
            Node endNode = null;
            for(Node node : nextNodes){
                if(node.getNodeDefId().equals("audit-"+execution.passStr)){
                    endNode = node;
                }
            }
            if(endNode == null){
                throw new EngineAccessException("can not find line..");
            }
            Line targetLine = flowInst.getLineMap().get(flowInst.getStartNode().getId()
                +","+endNode.getId());
            arg.targetLine = targetLine;
            arg.flowInst = flowInst;
        }
    }

    @Override
    // [logic] 流程起草步骤：先创建任务，然后直接完成任务，最后再创建任务（再创建任务在FinishTask中进行调用）
    public String executeEngineTask(EngineTask task) throws EngineRuntimeException {
        StartProc.StartProcArg arg = (StartProc.StartProcArg)task.getArgs();
        TaskExecution execution = arg.execution;
        ProcInst procInst = new ProcInst();
        procInst.setId(CommonUtils.genUUid());
        procInst.genBaseVariables();
        procInst.setFlowId(execution.flowId);
        procInst.setBussId(execution.bussId);
        procInst.setBussTable(arg.flowInst.getBussTableName());
        procInst.setCreateUser(execution.passUser);
        procInst.setFormKey(arg.flowInst.getFormKey());
        arg.procInstDao.save(procInst);

        Flow flowInst = super.getFlowComplete(arg.definationService,execution.flowId);
        //TODO cache oper

        if(isNotBlank(execution.passStr)){
            TaskExecution nextExcution = new TaskExecution();
            nextExcution.flowId  = execution.flowId;
            nextExcution.passUser = execution.passUser;
            nextExcution.passStr = execution.passStr;

            nextExcution.procInstId = procInst.getId();
            nextExcution.nodeDefId = flowInst.getStartNode().getNodeDefId();
            // [logic] 对于流程的第一个任务，一定是单人任务，所以无需指定assignUserList
            nextExcution.assignUser = execution.passUser;

            CreateTask.CreateTaskArg nextArg = new CreateTask.CreateTaskArg();
            nextArg.execution = nextExcution;
            nextArg.copyFrom(arg);

            String taskId = null;
            EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(CreateTask.class,nextArg,null);
            try {
                taskId = EngineManager.execute(engineTask);
            } catch (Exception e) {
                throw e;
            }

            // 测试事务是否能够正确回滚，结果可行
//            if(execution.passUser.equals("admin")){
//                throw new RuntimeException("test!....");
//            }

            nextExcution.passStr = execution.passStr;
            nextExcution.taskId = taskId;
            nextExcution.assignUser = execution.assignUser;
            nextExcution.assignUserList = execution.assignUserList;

            FinishTask.FinishTaskArg finishTaskArg = new FinishTask.FinishTaskArg();
            finishTaskArg.execution = nextExcution;
            finishTaskArg.copyFrom(arg);

            EngineTask  finishTask = EngineTaskTemplateFactory.buildEngineTask(FinishTask.class,finishTaskArg,null);
            try {
                taskId = EngineManager.execute(finishTask);
            } catch (Exception e) {
                throw e;
            }

        }
        task.setExecutedResult(procInst.getId());
        return null;
    }

    @Override
    public String handleCallback(EngineTask task) throws EngineRuntimeException {
        return (String)task.getExecutedResult();
    }




}

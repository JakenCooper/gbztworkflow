package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.LineDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.ProcInstDao;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.TaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.ProcInst;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineTaskTemplateFactory;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import java.util.List;

public class StartProc extends EngineBaseExecutor {

    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_STARTPROC_SYNC;

    private Logger logger = Logger.getLogger(StartProc.class);
    private static String LOGGER_TYPE_PREFIX = "StartProc,";

    public static class StartProcArg implements IEngineArg {
        private String[] requiredArg = new String[]{"flowId","bussId","passUser","formKey"};
        private String[] additionArg = new String[]{"passStr"};
        public DefinationService definationService;
        public TaskDao taskDao;
        public LineDao lineDao;
        public ProcInstDao procInstDao;
        public TaskExecution execution;

        private Flow flowInst;
        private Line targetLine;
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
        StartProc.StartProcArg arg = (StartProc.StartProcArg)iarg;
        engineTask.setArgs(arg);
        engineTask.setTaskId(CommonUtils.genUUid());
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
    public String executeEngineTask(EngineTask task) throws EngineRuntimeException {
        StartProc.StartProcArg arg = (StartProc.StartProcArg)task.getArgs();
        TaskExecution execution = arg.execution;
        ProcInst procInst = new ProcInst();
        procInst.genBaseVariables();
        procInst.setFlowId(execution.flowId);
        procInst.setBussId(execution.bussId);
        procInst.setBussTable(arg.flowInst.getBussTableName());
        procInst.setCreateUser(execution.passUser);
        arg.procInstDao.save(procInst);

        if(isNotBlank(execution.passStr)){

        }
        task.setExecutedResult(procInst.getId());
        return null;
    }

    @Override
    public String handleCallback(EngineTask task) throws EngineRuntimeException {
        return (String)task.getExecutedResult();
    }




}

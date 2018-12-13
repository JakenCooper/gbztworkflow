package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class GetBussTable extends EngineBaseExecutor {
    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_GETBUSSTABLE_SYNC;

    private Logger logger = Logger.getLogger(GetBussTable.class);
    private static String LOGGER_TYPE_PREFIX = "GetBussTable,";

    public static class GetBussTableArg extends EngineBaseArg implements IEngineArg{
        private String[] requiredArg = new String[]{};
        public TaskExecution execution;
    }

    @Override
    public EngineTask generateDefaultEngineTask(IEngineArg iarg, Object externalArg) {
        GetBussTable.GetBussTableArg arg = (GetBussTable.GetBussTableArg)iarg;
        EngineTask task = super.generateDefaultEngineTask(TASK_TYPE,arg);
        return task;
    }


    @Override
    public String executeEngineTask(EngineTask task) throws EngineRuntimeException {
        GetBussTable.GetBussTableArg arg = (GetBussTable.GetBussTableArg)task.getArgs();
        TaskExecution execution = arg.execution;

        task.setExecutedResult(arg.definationService.getAllFlowsForOA(execution.getProcInstId()));

        return "success";
    }

    @Override
    public List<Map<String,String>> handleCallback(EngineTask task) throws EngineRuntimeException {
        return (List<Map<String,String>>) task.getExecutedResult();
    }


}

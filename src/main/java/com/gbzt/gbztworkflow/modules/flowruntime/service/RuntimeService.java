package com.gbzt.gbztworkflow.modules.flowruntime.service;

import com.gbzt.gbztworkflow.modules.base.BaseService;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.LineDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.NodeDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.ProcInstDao;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.TaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.flowruntime.model.TaskModel;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineManager;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineTaskTemplateFactory;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.task.FinishTask;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.task.GetNextStep;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.task.GetUndo;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.task.StartProc;
import com.gbzt.gbztworkflow.utils.SimpleCache;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service(value="runtimeService")
public class RuntimeService extends BaseService implements  IRuntimeService  {

    private Logger logger = Logger.getLogger(RuntimeService.class);
    private static final String LOGGER_TYPE_PREFIX = "RuntimeService,";

    public RuntimeService(){
        super.setLogger(logger);
    }

    @Autowired
    private FlowDao flowDao;
    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private LineDao lineDao;
    @Autowired
    private DefinationService definationService;

    @Autowired
    private ProcInstDao procInstDao;
    @Autowired
    private TaskDao taskDao;



    /*
    *  rtn : List<Map> ensensial attrs of nodes
    * */
    public TaskModel getNextStep(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"fetchNextStep";

        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        GetNextStep.GetNextStepArg arg = new GetNextStep.GetNextStepArg();
        arg.execution = execution;
        arg.definationService = definationService;
        arg.taskDao = taskDao;

        EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(GetNextStep.class,arg,null);
        try {
            List<Map<String,String>> result = EngineManager.execute(engineTask);
            return (TaskModel) buildResult(model,true,"",result);
        } catch (Exception e) {
            return (TaskModel)buildResult(model,4,loggerType,null,e,
                    false,e.getMessage(),null);
        }

    }


    /*
     * rtn : procInstId
     * */
    public TaskModel startProc(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"startProc";

        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        StartProc.StartProcArg arg = new StartProc.StartProcArg();
        arg.execution = execution;
        arg.definationService = definationService;
        arg.procInstDao = procInstDao;
        arg.lineDao = lineDao;
        arg.taskDao = taskDao;

        EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(StartProc.class,arg,null);
        try {
            String result = EngineManager.execute(engineTask);
            return (TaskModel) buildResult(model,true,"",result);
        } catch (Exception e) {
            return (TaskModel)buildResult(model,4,loggerType,null,e,
                    false,e.getMessage(),null);
        }
    }

    /*
     * rtn : List<Map> ensensial attrs of tasks
     * */
    public TaskModel finishTask(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"finishTask";

        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        FinishTask.FinishTaskArg arg = new FinishTask.FinishTaskArg();
        arg.execution = execution;
        arg.definationService = definationService;
        arg.procInstDao = procInstDao;
        arg.taskDao = taskDao;

        EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(FinishTask.class,arg,model.getArgMap());
        try {
            String result = EngineManager.execute(engineTask);
            return (TaskModel) buildResult(model,true,"",result);
        } catch (Exception e) {
            return (TaskModel)buildResult(model,4,loggerType,null,e,
                    false,e.getMessage(),null);
        }
    }


    /*
     *  rtn : List<Map> ensensial attrs of tasks
     * */
    public TaskModel getUndo(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"getUndo";

        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        GetUndo.GetUndoArg arg = new GetUndo.GetUndoArg();
        arg.execution = execution;
        arg.taskDao = taskDao;
        arg.taskModel = model;

        EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(GetUndo.class,arg,null);
        try {
           TaskModel result = EngineManager.execute(engineTask);
           //return result directly,result already setted in executor;
           return result;
        } catch (Exception e) {
            return (TaskModel)buildResult(model,4,loggerType,null,e,
                    false,e.getMessage(),null);
        }

    }

}

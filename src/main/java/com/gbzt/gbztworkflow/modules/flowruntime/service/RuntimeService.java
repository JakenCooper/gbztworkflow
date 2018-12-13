package com.gbzt.gbztworkflow.modules.flowruntime.service;

import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.affairConfiguer.dao.AffairConfiguerDao;
import com.gbzt.gbztworkflow.modules.affairConfiguer.entity.AffairConfiguer;
import com.gbzt.gbztworkflow.modules.affairConfiguer.service.AffairConfiguerService;
import com.gbzt.gbztworkflow.modules.base.BaseService;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.LineDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.NodeDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.flowdefination.service.UserNodePrivService;
import com.gbzt.gbztworkflow.modules.flowruntime.model.UserTreeInfo;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.*;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.flowruntime.model.TaskModel;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineManager;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineTaskTemplateFactory;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.task.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private UserNodePrivService nodeUserPrivService;

    @Autowired
    private ProcInstDao procInstDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskVariableDao taskVariableDao;
    @Autowired
    private HistTaskDao histTaskDao;
    @Autowired
    private HistProcDao histProcDao;
    @Autowired
    private AffairConfiguerDao ad;


    /**
     *  @author 小白白
     *  统一为参数对象添加业务对象，要求必须是 EngineBaseArg 的子类，否则不会添加任何业务对象
     * */
    public void setDefaultArgValue(IEngineArg targetArg){
        EngineBaseArg targetBaseArg = null;
        try {
             targetBaseArg = (EngineBaseArg)targetArg;
        } catch (Exception e) {
            return ;
        }
        if(targetBaseArg == null){
            return ;
        }
        targetBaseArg.flowDao = this.flowDao;
        targetBaseArg.nodeDao = this.nodeDao;
        targetBaseArg.lineDao = this.lineDao;
        targetBaseArg.definationService = this.definationService;
        targetBaseArg.nodeUserPrivService = this.nodeUserPrivService;

        targetBaseArg.procInstDao = this.procInstDao;
        targetBaseArg.taskDao = this.taskDao;
        targetBaseArg.taskVariableDao = this.taskVariableDao;
        targetBaseArg.histTaskDao = this.histTaskDao;
        targetBaseArg.histProcDao = this.histProcDao;
        targetBaseArg.ad = this.ad;
    }

    /*
    *  rtn : List<Map> ensensial attrs of nodes
    * */
    public TaskModel getNextStep(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"fetchNextStep";

        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        GetNextStep.GetNextStepArg arg = new GetNextStep.GetNextStepArg();
        arg.execution = execution;
        setDefaultArgValue(arg);

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
    @Transactional("jtm")
    public TaskModel startProc(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"startProc";

        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        execution.setArgMap(model.getArgMap());
        StartProc.StartProcArg arg = new StartProc.StartProcArg();
        arg.execution = execution;
        setDefaultArgValue(arg);

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
     * rtn : too complicate to calculate,so just return tag
     * */
    @Transactional("jtm")
    public TaskModel finishTask(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"finishTask";

        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        execution.setArgMap(model.getArgMap());
        FinishTask.FinishTaskArg arg = new FinishTask.FinishTaskArg();
        arg.execution = execution;
        setDefaultArgValue(arg);

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
    @Transactional("jtm")
    public TaskModel getUndo(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"getUndo";

        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        execution.setArgMap(model.getArgMap());
        GetUndo.GetUndoArg arg = new GetUndo.GetUndoArg();
        arg.execution = execution;
        arg.taskModel = model;
        setDefaultArgValue(arg);

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


    /*
     *  rtn : List<UserTreeInfo>
     * */
    public TaskModel getUserNodeData(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"getUserNodeData";

        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        execution.setArgMap(model.getArgMap());
        GetUserNodeData.GetUserNodeDataArg arg = new GetUserNodeData.GetUserNodeDataArg();
        arg.execution = execution;
        setDefaultArgValue(arg);

        EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(GetUserNodeData.class,arg,null);
        try {
            List<UserTreeInfo> result = EngineManager.execute(engineTask);
            return (TaskModel) buildResult(model,true,"",result);
        } catch (Exception e) {
            return (TaskModel)buildResult(model,4,loggerType,null,e,
                    false,e.getMessage(),null);
        }
    }



    /*
     *  rtn : List<Map> ensensial attrs of tasks
     * */
    public TaskModel getHistTask(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"getHistTask";

        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        GetHistTask.GetHistTaskArg arg = new GetHistTask.GetHistTaskArg();
        arg.execution = execution;
        arg.taskModel = model;
        setDefaultArgValue(arg);

        EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(GetHistTask.class,arg,null);
        try {
            TaskModel result = EngineManager.execute(engineTask);
            //return result directly,result already setted in executor;
            return result;
        } catch (Exception e) {
            return (TaskModel)buildResult(model,4,loggerType,null,e,
                    false,e.getMessage(),null);
        }

    }



    /*
     *  rtn : List<Map> ensensial attrs of tasks
     * */
    public TaskModel getProcHistoric(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"getProcHistoric";

        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        GetProcHistoric.GetProcHistoricArg arg = new GetProcHistoric.GetProcHistoricArg();
        arg.execution = execution;
        setDefaultArgValue(arg);

        EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(GetProcHistoric.class,arg,null);
        try {
            List<Map<String,Object>> result = EngineManager.execute(engineTask);
            return (TaskModel)buildResult(model,true,"",result);
        } catch (Exception e) {
            return (TaskModel)buildResult(model,4,loggerType,null,e,
                    false,e.getMessage(),null);
        }
    }

    /*
     *  retreatSubmitTag : false
     *  rtn : boolean
     * */
    public TaskModel canRetreat(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"canRetreat";

        if(StringUtils.isBlank(model.getRetreatOperType())){
            throw new IllegalArgumentException("illegal retreat oper type.");
        }
        if(model.isRetreatSubmitTag()){
            throw new IllegalArgumentException("wrong interface call.");
        }
        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        RetreatTask.RetreatTaskArg arg = new RetreatTask.RetreatTaskArg();
        arg.execution = execution;
        setDefaultArgValue(arg);

        EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(RetreatTask.class,arg,null);
        try {
            boolean result = EngineManager.execute(engineTask);
            return (TaskModel)buildResult(model,true,"",result);
        } catch (Exception e) {
            return (TaskModel)buildResult(model,4,loggerType,null,e,
                    false,e.getMessage(),null);
        }
    }

    /*
     *  retreatSubmitTag : true
     *  rtn : String
     * */
    @Transactional("jtm")
    public TaskModel retreatSubmit(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"retreatSubmit";

        if(StringUtils.isBlank(model.getRetreatOperType())){
            throw new IllegalArgumentException("illegal retreat oper type.");
        }
        if(!model.isRetreatSubmitTag()){
            throw new IllegalArgumentException("wrong interface call.");
        }
        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        RetreatTask.RetreatTaskArg arg = new RetreatTask.RetreatTaskArg();
        arg.execution = execution;
        setDefaultArgValue(arg);

        EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(RetreatTask.class,arg,null);
        try {
            String result = EngineManager.execute(engineTask);
            System.out.println("result of retreat tag === "+result);
            return (TaskModel)buildResult(model,true,"",result);
        } catch (Exception e) {
            return (TaskModel)buildResult(model,4,loggerType,null,e,
                    false,e.getMessage(),null);
        }
    }


    public TaskModel getBussTable(TaskModel model) {
        String loggerType = LOGGER_TYPE_PREFIX+"getBussTable";
        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(model,execution);
        GetBussTable.GetBussTableArg arg = new GetBussTable.GetBussTableArg();
        arg.execution = execution;
        setDefaultArgValue(arg);

        EngineTask  engineTask = EngineTaskTemplateFactory.buildEngineTask(GetBussTable.class,arg,null);
        try {
            List<Map<String,String>> result = EngineManager.execute(engineTask);
            return (TaskModel)buildResult(model,true,"",result);
        } catch (Exception e) {
            return (TaskModel)buildResult(model,4,loggerType,null,e,
                    false,e.getMessage(),null);
        }
    }

    public List<TaskModel> getAffairConfiguerList(TaskModel taskModel) {
        System.out.println("进入getAffairConfiguerList");
        List<AffairConfiguer> affairConfiguers=new ArrayList<>();
        List<TaskModel> taskModelList=new ArrayList<>();
        try {
            affairConfiguers= ad.findAffairConfiguerByFlowId(taskModel.getFlowId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i=0;i<affairConfiguers.size();i++){
             TaskModel tm=new TaskModel();
             tm.setChColName(affairConfiguers.get(i).getChColName());
             tm.setSearchType(affairConfiguers.get(i).getSearchType());
             tm.setColName(affairConfiguers.get(i).getColName());
             String isUsed=affairConfiguers.get(i).getIsUsed();
             if("禁用".equals(isUsed)||StringUtils.isBlank(isUsed)){
                 continue;
             }
            taskModelList.add(tm);
        }
        return taskModelList;
    }



    public TaskModel findFlowByFlowName(TaskModel taskModel){
        System.out.println("进入findFlowByFlowName");
        Flow flow=flowDao.findFlowByFlowName(taskModel.getFlowName());
        if(flow!=null){
            taskModel.setFlowId(flow.getId());
        }
        return taskModel;
    }
}

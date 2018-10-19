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
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.task.GetNextStep;
import com.gbzt.gbztworkflow.utils.SimpleCache;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RuntimeService extends BaseService {

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
    public TaskModel fetchNextStep(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"fetchNextStep";

        TaskExecution execution = new TaskExecution();
        BeanUtils.copyProperties(execution,model);
        GetNextStep.GetNextStepArg arg = new GetNextStep.GetNextStepArg();
        arg.execution = execution;
        arg.definationService = definationService;

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
        //TODO valid flowid null passStr null passUser null
        definationService.generateDetailDefination(model.getFlowId());
        Flow flowInst = (Flow)SimpleCache.getFromCache(SimpleCache.CACHE_KEY_PREFIX_FLOW_DETAIL+model.getFlowId());
        if(flowInst == null || flowInst.getStartNode() == null || flowInst.getStartNode().getNextNodes().size() == 0){
            return (TaskModel)buildResult(model,4,loggerType,null
                    ,new IllegalArgumentException(flowInst.getFlowName()+"no nodes next to start node"),
                    false,"no nodes next to start node",null);
        }
        List<Node> nextNodes = flowInst.getStartNode().getNextNodes();
        Node desNode = null;
        for(Node node : nextNodes){
            if(node.getNodeDefId().equals("audit-"+model.getPassStr())){
                desNode = node;
            }
        }
        if(desNode == null){
            return (TaskModel)buildResult(model,4,loggerType,null
                    ,new IllegalArgumentException(flowInst.getFlowName()+"wrong passStr ..."),
                    false,"wrong passStr ...",null);
        }
        List<Line> lines = lineDao.findLinesByBeginNodeIdAndEndNodeId(flowInst.getStartNode().getId()
                ,desNode.getId());

        return null;
    }

}

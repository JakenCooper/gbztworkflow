package com.gbzt.gbztworkflow.modules.flowruntime.service;

import com.gbzt.gbztworkflow.modules.base.BaseService;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.LineDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.NodeDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.flowruntime.entity.Task;
import com.gbzt.gbztworkflow.modules.flowruntime.model.BaseModel;
import com.gbzt.gbztworkflow.modules.flowruntime.model.TaskModel;
import com.gbzt.gbztworkflow.utils.LogUtils;
import com.gbzt.gbztworkflow.utils.SimpleCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RuntimeService extends BaseService {

    private Logger logger = Logger.getLogger(RuntimeService.class);
    private static final String LOGGER_TYPE_PREFIX = "RuntimeService,";

    @Autowired
    private FlowDao flowDao;
    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private LineDao lineDao;
    @Autowired
    private DefinationService definationService;


    private static final String ERROR_RUNTIME_

    /*
    *  rtn : List<Map> ensensial attrs of nodes
    * */
    public TaskModel fetchNextStep(TaskModel model){
        String loggerType = LOGGER_TYPE_PREFIX+"fetchNextStep";
        if(StringUtils.isBlank(model.getProcInstId())){
            Flow flowInst = flowDao.findFlowByFlowName(model.getFlowName());

            definationService.generateDetailDefination(flowInst.getId());
            flowInst = (Flow)SimpleCache.getFromCache(SimpleCache.CACHE_KEY_PREFIX_FLOW_DETAIL
                    +flowInst.getId());

            if(flowInst == null){
                return (TaskModel)buildResult(model,4,loggerType,null,new IllegalArgumentException("get null from cache.."),
                        false,"server internal error!",null);
            }
            Node startNode = flowInst.getStartNode();
            if(startNode == null){
                return (TaskModel)buildResult(model,4,loggerType,null,new IllegalArgumentException("no start node ..."),
                        false,"no start node ...",null);
            }
            List<Map<String,String>> result = new ArrayList<Map<String,String>>();
            if(startNode.getNextNodes() == null || startNode.getNextNodes() .size() == 0){
                return (TaskModel)buildResult(model,4,loggerType,null,new IllegalArgumentException("no nodes next to start node.."),
                        false,"no nodes next to start node..",null);
            }
            for(Node node : startNode.getNextNodes()){
                Map<String,String> nodeMap = new HashMap<String,String>();
                nodeMap.put("nodeId",node.getId());
                nodeMap.put("nodeDefId",node.getNodeDefId());
                nodeMap.put("nodeName",node.getName());
                nodeMap.put("flowId",node.getFlowId());
                result.add(nodeMap);
            }
            return (TaskModel) buildResult(model,true,"",result);
        }else{
            //TODO fetch due to procinstid
            return model;
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


    }

}

package com.gbzt.gbztworkflow.modules.flowdefination.service;

import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.affairConfiguer.service.AffairConfiguerService;
import com.gbzt.gbztworkflow.modules.base.BaseService;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowBussDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.LineDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.NodeDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.redis.service.JedisService;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.ProcInstDao;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import com.gbzt.gbztworkflow.utils.LogUtils;
import com.gbzt.gbztworkflow.utils.SimpleCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DefinationCacheService extends BaseService {

    private Logger logger = Logger.getLogger(DefinationCacheService.class);
    private static String LOGGER_TYPE_PREFIX = "DefinationCacheService,";

    @Autowired
    private FlowDao flowDao;
    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private LineDao lineDao;
    @Autowired
    private FlowBussDao flowBussDao;
    @Autowired
    private ProcInstDao procInstDao;
    @Autowired
    private AffairConfiguerService affairConfiguerService;

    @Autowired
    private JedisService jedisService;



    /**
     *  -----------------------------------   flow related begin -----------------------------------
     * */
    @Transactional("jtm")
    public ExecResult saveFlow(Flow flow){
        if(StringUtils.isBlank(flow.getFlowName())){
            return buildResult(false,"流程名称为空",null);
        }
        if(StringUtils.isBlank(flow.getBussTableName())){
            return buildResult(false,"业务表为空",null);
        }
        if(flow.getBussColumns() == null || flow.getBussColumns().size() == 0){
            return buildResult(false,"没有选择业务字段",null);
        }
        //避免奇奇怪怪的问题
        flow.setFlowName(flow.getFlowName().replaceAll("!","！"));
        boolean isnew = false;
        if(StringUtils.isBlank(flow.getId())){
            flow.setId(CommonUtils.genUUid());
            isnew = true;
        }
        if(isnew) {
            if (jedisService.countFlowByFlowName(flow.getFlowName()) > 0) {
                return buildResult(false, "流程名称重复", null);
            }
        }

        if(StringUtils.isBlank(flow.getFormKey())){
            StringBuffer formBuffer = new StringBuffer("/");
            formBuffer.append(flow.getModuleName()).append("/").append(CommonUtils.convertTableName(flow.getBussTableName()))
                    .append("/").append("form");
            flow.setFormKey(formBuffer.toString());
        }
        if(isnew) {
            flow.genBaseVariables();
        }else{
            flow.setUpdateTime(new Date());
        }
        jedisService.saveFlow(flow);
        String[] bussarr = new String[flow.getBussColumns().size()];
        affairConfiguerService.save(flow.getId(),flow.getBussColumns().toArray(bussarr));

        return buildResult(true,"保存成功",null);
    }

    public ExecResult<Flow> getFlowById(String id){
        if(StringUtils.isBlank(id)){
            return buildResult(false,"查询id为空",null);
        }
        return buildResult(true,"",jedisService.findFlowByIdOrName(id,null));
    }

    public Flow getFlowByName(String flowName){
        return jedisService.findFlowByIdOrName(null,flowName);
    }

    public List<Map<String,String>> getAllFlowsForOA(String procInstId){
        List<Flow> flows = jedisService.findFlowsByDelTag(false);
        List<Map<String,String>> flowList = new ArrayList<Map<String,String>>();
        Flow targetFlow = null;
        if(StringUtils.isNotBlank(procInstId)){
            targetFlow = jedisService.findFlowByIdOrName(jedisService.findProcInstById(procInstId).getFlowId(),null);
        }
        for(Flow flow:flows){
            if(StringUtils.isBlank(procInstId)){
                Map<String,String> flowMap = new HashMap<String,String>();
                flowMap.put("id",flow.getId());
                flowMap.put("name",flow.getFlowName());
                flowMap.put("bussTable",flow.getBussTableName());
                flowMap.put("formkey",flow.getFormKey());
                flowList.add(flowMap);
            }else{
                if(targetFlow.getId().equals(flow.getId())){
                    Map<String,String> flowMap = new HashMap<String,String>();
                    flowMap.put("id",flow.getId());
                    flowMap.put("name",flow.getFlowName());
                    flowMap.put("bussTable",flow.getBussTableName());
                    flowMap.put("formkey",flow.getFormKey());
                    flowList.add(flowMap);
                }
            }
        }
        return flowList;
    }

    public ExecResult<List<Flow>> getAllFlows(){
        return buildResult(true,"",jedisService.findFlowsByDelTag(false));
    }

    @Transactional("jtm")
    public ExecResult delFlow(Flow flow){
        if(flow == null || StringUtils.isBlank(flow.getId())){
            return buildResult(false,"id为空，删除失败",null);
        }
        jedisService.delFlow(flow.getId());
        return buildResult(true,"删除成功",null);
    }

    /**
     *  ==================   flow related end ===================
     * */


    /**
     *  -----------------------------------   node related begin -----------------------------------
     * */
    public ExecResult<List<Node>> getNodesByFlowId(String flowId){
        if(StringUtils.isBlank(flowId)){
            return buildResult(false,"流程id为空",null);
        }
        return buildResult(true,"",nodeDao.findNodeByFlowIdOrderByNodeDefIdDesc(flowId));
    }

    public Node getNodeByIdSimple(String nodeId){
        return nodeDao.findOne(nodeId);
    }

    public ExecResult<Node> getNodeById(String nodeId){
        if(StringUtils.isBlank(nodeId)){
            return buildResult(false,"节点id为空",null);
        }
        Node node = nodeDao.findOne(nodeId);
        // 查询关于节点的详细信息
        node.setAllNodesInFlow(nodeDao.findNodeByFlowIdOrderByNodeDefIdDesc(node.getFlowId()));
        node.setOutLines(lineDao.findLinesByBeginNodeId(node.getId()));
        node.setInLines(lineDao.findLinesByEndNodeId(node.getId()));
        if(node.getOutLines() != null && node.getOutLines().size() > 0){
            List<String> nextNodesIds = new ArrayList<String>();
            for(Line line : node.getOutLines()){
                nextNodesIds.add(line.getEndNodeId());
            }
            node.setNextNodes(nodeDao.findNodesByIdIn(nextNodesIds));
        }
        if(node.getInLines() != null && node.getInLines().size() > 0){
            List<String> foreNodesIds = new ArrayList<String>();
            for(Line line : node.getInLines()){
                foreNodesIds.add(line.getBeginNodeId());
            }
            node.setForeNodes(nodeDao.findNodesByIdIn(foreNodesIds));
        }
        return buildResult(true,"",node);
    }

    @Transactional("jtm")
    public ExecResult saveNode(Node node){
        if(StringUtils.isBlank(node.getName())){
            return buildResult(false,"节点名称为空",null);
        }
        if(StringUtils.isBlank(node.getFlowId())){
            return buildResult(false,"流程id为空",null);
        }
        if(nodeDao.countNodeByNameAndFlowId(node.getName(),node.getFlowId()) > 0){
            return buildResult(false,"改流程下节点名称重复",null);
        }
        if(StringUtils.isBlank(node.getId())){
            node.setId(CommonUtils.genUUid());
        }
        if(node.getSortNum() == null){
            node.setSortNum(1);
        }
        node.genBaseVariables();
        nodeDao.save(node);
        refreshDetailDefination(node.getFlowId());
        return buildResult(true,"保存成功",null);
    }

    @Transactional("jtm")
    public ExecResult updateNode(String nodeId,Node node){
        if(StringUtils.isBlank(node.getName())){
            return buildResult(false,"节点名称为空",null);
        }
        if(StringUtils.isBlank(node.getFlowId())){
            return buildResult(false,"流程id为空",null);
        }
        node.setId(nodeId);
        if(node.getSortNum() == null){
            node.setSortNum(1);
        }
        node.genBaseVariables();
        nodeDao.save(node);
        refreshDetailDefination(node.getFlowId());
        return buildResult(true,"修改成功",null);
    }

    @Transactional("jtm")
    public ExecResult delNode(Node node){
        node = nodeDao.findOne(node.getId());
        if(node == null || StringUtils.isBlank(node.getId())){
            return buildResult(false,"id为空，删除失败",null);
        }
        // delete all lines related to this node.
        List<Line> inLineList = lineDao.findLinesByBeginNodeId(node.getId());
        List<Line> outLineList = lineDao.findLinesByEndNodeId(node.getId());
        try {
            if(inLineList != null && inLineList.size() > 0){
                for(Line inline : inLineList){
                    ExecResult execResult = delLine(inline);
                    if(!execResult.charge){
                        throw new IllegalAccessException("");
                    }
                }
            }
            if(outLineList != null && outLineList.size() > 0){
                for(Line outline : outLineList){
                    ExecResult execResult = delLine(outline);
                    if(!execResult.charge){
                        throw new IllegalAccessException("");
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return buildResult(false,"连线删除失败",null);
        }

        nodeDao.delete(node.getId());
        refreshDetailDefination(node.getFlowId());
        return buildResult(true,"删除成功",null);
    }



    /**
     *
     *   ==================   node related end =================
     * */

    /**
     *  -----------------------------------   line related begin -----------------------------------
     * */

    public ExecResult delLine(Line line){
        if(line == null || StringUtils.isBlank(line.getBeginNodeId()) || StringUtils.isBlank(line.getEndNodeId())){
            return buildResult(false,"缺少节点数据，删除失败",null);
        }
        List<Line> currentLines = lineDao.findLinesByBeginNodeIdAndEndNodeId(line.getBeginNodeId(),line.getEndNodeId());
        if(currentLines !=null && currentLines.size()>0){
            for(Line currentLine : currentLines){
//                currentLine.setDelTag(true);
//                lineDao.save(currentLine);
                lineDao.delete(currentLine);
            }
        }
        refreshDetailDefination(line.getFlowId());
        return buildResult(true,"删除成功",null);
    }

    @Transactional("jtm")
    public ExecResult saveLine(Line line){
        if(line == null || StringUtils.isBlank(line.getFlowId())){
            return buildResult(false,"流程id为空，添加连线失败",null);
        }
        if(StringUtils.isBlank(line.getBeginNodeId()) || StringUtils.isBlank(line.getEndNodeId())
                || StringUtils.isBlank(line.getBeginNodeName()) || StringUtils.isBlank(line.getEndNodeName())){
            return buildResult(false,"节点数据不全，添加失败",null);
        }
        delLine(new Line(line.getBeginNodeId(),line.getEndNodeId()));
        if(StringUtils.isBlank(line.getId())){
            line.setId(CommonUtils.genUUid());
        }
        line.genBaseVariables();
        lineDao.save(line);
        refreshDetailDefination(line.getFlowId());
        return buildResult(true,"保存成功",null);
    }

    /**
     *
     *  ==================   line related end =================
     * */

    private void refreshDetailDefination(String flowId){
        String loggerType = LOGGER_TYPE_PREFIX+"refreshDetailDefination";
        String key = "flow_defination_detail_"+flowId;
        if(SimpleCache.inCache(key)){
            SimpleCache.remove(key);
        }
    }

    public void generateDetailDefination(String flowId){
        String loggerType = LOGGER_TYPE_PREFIX+"generateDetailDefination";
        String key = SimpleCache.CACHE_KEY_PREFIX_FLOW_DETAIL+flowId;
        if(SimpleCache.inCache(key)){
            return ;
        }
        ExecResult flowExecResult = getFlowById(flowId);
        if(!flowExecResult.charge){
            logger.error(LogUtils.getMessage(loggerType,flowExecResult.message));
            return ;
        }
        Flow flowInst = (Flow)flowExecResult.result;
        List<Node> allFlowNode = nodeDao.findNodeByFlowIdOrderByCreateTimeDesc(flowId);
        Node.sortNodes(allFlowNode);
        List<Line> allLines = new ArrayList<Line>();
        Map<String,Node> nodeMap = new HashMap<String,Node>();
        Map<String,Node> nodeIdMap = new HashMap<String,Node>();
        for(Node node : allFlowNode){
            if(node.isBeginNode()){
                flowInst.setStartNode(node);
            }
            if(node.isEndNode()){
                flowInst.setEndNode(node);
            }
            node.setOutLines(lineDao.findLinesByBeginNodeId(node.getId()));
            node.setInLines(lineDao.findLinesByEndNodeId(node.getId()));

            //insert lines.
            allLines.addAll(node.getOutLines());

            if(node.getOutLines() != null && node.getOutLines().size() > 0){
                List<String> nextNodesIds = new ArrayList<String>();
                for(Line line : node.getOutLines()){
                    nextNodesIds.add(line.getEndNodeId());
                }
                node.setNextNodes(Node.sortNodes(nodeDao.findNodesByIdIn(nextNodesIds)));
            }
            if(node.getInLines() != null && node.getInLines().size() > 0){
                List<String> foreNodesIds = new ArrayList<String>();
                for(Line line : node.getInLines()){
                    foreNodesIds.add(line.getBeginNodeId());
                }
                node.setForeNodes(Node.sortNodes(nodeDao.findNodesByIdIn(foreNodesIds)));
            }
            nodeMap.put(node.getName(),node);
            nodeIdMap.put(node.getId(),node);
        }
        flowInst.setAllNodes(allFlowNode);
        flowInst.setNodeMap(nodeMap);
        flowInst.setNodeIdMap(nodeIdMap);

        flowInst.setAllLines(allLines);
        Map<String,Line> lineMap = new HashMap<String,Line>();
        for(Line line : allLines){
            lineMap.put(line.getBeginNodeId()+","+line.getEndNodeId(),line);
        }
        flowInst.setLineMap(lineMap);

        SimpleCache.putIntoCache(key,flowInst);
    }
}

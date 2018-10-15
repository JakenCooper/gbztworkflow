package com.gbzt.gbztworkflow.modules.flowdefination.service;

import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.base.BaseService;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.LineDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.NodeDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefinationService extends BaseService {

    @Autowired
    private FlowDao flowDao;
    @Autowired
    private NodeDao nodeDao;
    @Autowired
    private LineDao lineDao;



    /**
     *  -----------------------------------   flow related begin -----------------------------------
     * */
    @Transactional("jtm")
    public ExecResult saveFlow(Flow flow){
        if(StringUtils.isBlank(flow.getName())){
            return buildResult(false,"流程名称为空",null);
        }
        if(flowDao.countFlowByName(flow.getName()) > 0){
            return buildResult(false,"流程名称重复",null);
        }
        if(StringUtils.isBlank(flow.getId())){
            flow.setId(CommonUtils.genUUid());
        }
        flow.genCreateTime();
        flowDao.save(flow);
        return buildResult(true,"保存成功",null);
    }

    public ExecResult<Flow> getFlowById(String id){
        if(StringUtils.isBlank(id)){
            return buildResult(false,"查询id为空",null);
        }
        return buildResult(true,"",flowDao.findOne(id));
    }

    public ExecResult<List<Flow>> getAllFlows(){
        return buildResult(true,"",flowDao.findAll());
    }

    @Transactional("jtm")
    public ExecResult delFlow(Flow flow){
        if(flow == null || StringUtils.isBlank(flow.getId())){
            return buildResult(false,"id为空，删除失败",null);
        }
        flowDao.delete(flow.getId());
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
        return buildResult(true,"",nodeDao.findNodeByFlowIdOrderByCreateTimeDesc(flowId));
    }

    public ExecResult<Node> getNodeById(String nodeId){
        if(StringUtils.isBlank(nodeId)){
            return buildResult(false,"节点id为空",null);
        }
        Node node = nodeDao.findOne(nodeId);
        // 查询关于节点的详细信息
        node.setAllNodesInFlow(nodeDao.findNodeByFlowIdOrderByCreateTimeDesc(node.getFlowId()));
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
        node.genCreateTime();
        nodeDao.save(node);
        return buildResult(true,"保存成功",null);
    }

    @Transactional("jtm")
    public ExecResult delNode(Node node){
        if(node == null || StringUtils.isBlank(node.getId())){
            return buildResult(false,"id为空，删除失败",null);
        }
        nodeDao.delete(node.getId());
        // TODO del all lines related to this node
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
                lineDao.delete(currentLine);
            }
        }
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
        line.genCreateTime();
        lineDao.save(line);
        return buildResult(true,"保存成功",null);
    }

    /**
     *
     *  ==================   line related end =================
     * */
}

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
import net.sf.ezmorph.Morpher;
import net.sf.ezmorph.MorpherRegistry;
import net.sf.ezmorph.bean.BeanMorpher;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.NewBeanInstanceStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
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

        refreshDetailDefination(flow.getId());
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
        refreshDetailDefination(flow.getId());
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
        return buildResult(true,"",jedisService.findNodeByFlowIdOrderByDefIdDesc(flowId));
    }

    public Node getNodeByIdSimple(String nodeId){
        return jedisService.findNodeById(nodeId);
    }

    public ExecResult<Node> getNodeById(String nodeId){
        if(StringUtils.isBlank(nodeId)){
            return buildResult(false,"节点id为空",null);
        }
        Node node = jedisService.findNodeById(nodeId);
        // 查询关于节点的详细信息
        node.setAllNodesInFlow(jedisService.findNodeByFlowIdOrderByDefIdDesc(node.getFlowId()));
        jedisService.findAndSetNodeWholeAttributes(node);
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
        if(jedisService.counterNodeNameByFlowId(node.getFlowId(),node.getName()) > 0){
            return buildResult(false,"改流程下节点名称重复",null);
        }
        if(StringUtils.isBlank(node.getId())){
            node.setId(CommonUtils.genUUid());
        }
        if(node.getSortNum() == null){
            node.setSortNum(1);
        }
        node.genBaseVariables();
        jedisService.saveNode(node);
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
        jedisService.saveNode(node);
        refreshDetailDefination(node.getFlowId());
        return buildResult(true,"修改成功",null);
    }

    @Transactional("jtm")
    public ExecResult delNode(Node node){
        node = jedisService.findNodeById(node.getId());
        if(node == null || StringUtils.isBlank(node.getId())){
            return buildResult(false,"id为空，删除失败",null);
        }
        // delete all lines related to this node.
        jedisService.delNode(node);
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
        Line currentLine = jedisService.findLineByBeginNodeIdAndEndNodeId(line.getBeginNodeId(),line.getEndNodeId());
        if(currentLine != null){
            jedisService.delLine(currentLine);
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
        if(StringUtils.isBlank(line.getId())){
            line.setId(CommonUtils.genUUid());
        }
        line.genBaseVariables();
        jedisService.saveLine(line);
        refreshDetailDefination(line.getFlowId());
        return buildResult(true,"保存成功",null);
    }

    /**
     *
     *  ==================   line related end =================
     * */

    private void refreshDetailDefination(String flowId){
        String loggerType = LOGGER_TYPE_PREFIX+"refreshDetailDefination";
        String key = JedisService.CACHE_KEY_PREFIX_FLOW_DETAIL+flowId;
        jedisService.setCachedString(key,null);
    }

    public void generateDetailDefination(String flowId){
        String loggerType = LOGGER_TYPE_PREFIX+"generateDetailDefination";
        String key = JedisService.CACHE_KEY_PREFIX_FLOW_DETAIL+flowId;
        if(jedisService.findCachedString(key) != null){
            return ;
        }
        Flow flowInst = jedisService.findFlowByIdOrName(flowId,null);
        if(flowInst == null){
            logger.error(LogUtils.getMessage(loggerType,"todo cache is null,flowid === "+flowId));
        }
        List<Node> allFlowNode = jedisService.findNodeByFlowIdOrderByDefIdDesc(flowId);
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
            jedisService.findAndSetNodeWholeAttributes(node);
            //insert lines.
            allLines.addAll(node.getOutLines());

            nodeMap.put(node.getName(),node);
            nodeIdMap.put(node.getId(),node);
        }
        flowInst.setAllNodes(allFlowNode);
//        flowInst.setNodeMap(nodeMap);
//        flowInst.setNodeIdMap(nodeIdMap);

        flowInst.setAllLines(allLines);
        Map<String,Line> lineMap = new HashMap<String,Line>();
        for(Line line : allLines){
            lineMap.put(line.getBeginNodeId()+","+line.getEndNodeId(),line);
        }
        flowInst.setLineMap(lineMap);

        jedisService.setCachedString(key,JSONObject.fromObject(flowInst).toString());

    }

    public Flow findDetailDefination(String flowId){
        String key = JedisService.CACHE_KEY_PREFIX_FLOW_DETAIL+flowId;
        String loggerType = LOGGER_TYPE_PREFIX+"findDetailDefination";

        Flow flowInst = null;
        String cachedValue = jedisService.findCachedString(key);
        if(StringUtils.isBlank(cachedValue)){
            return null;
        }
        try {
            JSONObject jsonObject = JSONObject.fromObject(cachedValue);
            JsonConfig jsonConfig = new JsonConfig();
            Map<String,Class> classMap = new HashMap<>();
            jsonConfig.setRootClass(Flow.class);
            classMap.put("allNodes",Node.class);
            classMap.put("allLines",Line.class);
            classMap.put("outLines",Line.class);
            classMap.put("inLines",Line.class);
            classMap.put("nextNodes",Node.class);
            classMap.put("foreNodes",Node.class);
            classMap.put("allNodesInFlow",Node.class);
            jsonConfig.setClassMap(classMap);

            flowInst = (Flow)JSONObject.toBean(jsonObject,jsonConfig);
            if(flowInst.getAllNodes() != null && flowInst.getAllNodes().size() > 0){
                for(Node node : flowInst.getAllNodes()){
                    flowInst.getNodeMap().put(node.getName(),node);
                    flowInst.getNodeIdMap().put(node.getId(),node);
                }
            }
            if(flowInst.getAllLines() != null && flowInst.getAllLines().size() > 0){
                for(Line line : flowInst.getAllLines()){
                    flowInst.getLineMap().put(line.getId(),line);
                }
            }
            return flowInst;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(LogUtils.getMessage(loggerType,"缓存解析错误，缓存的当前值是："
                    +cachedValue),e);
        }
        return null;
    }


    public static void main(String[] args) {
        Flow flow = new Flow();
        flow.setId("12321321");
        flow.setFlowName("我是白痴");
        flow.setFormKey("/fdasfdsa/fdsafdsa/fdasfdsa");
        List<String> bussColumn =new ArrayList<String>();
        bussColumn.add("col1");
        bussColumn.add("col2");
        flow.setBussColumns(bussColumn);
        List<Node> allnodes =new ArrayList<Node>();
        Node node1 = new Node();
        node1.setId("bcxzvcxz1");
        node1.setSortNum(3);
        node1.setBeginNode(true);
        Node node2 = new Node();
        node2.setId("bbbbbbbb");
        node2.setSortNum(2);
        node2.setBeginNode(true);
        node2.setBeginNodeStr("true");

        node1.getNextNodes().add(node2);
        Line line =new Line();
        line.setCanRetreat(true);
        line.setCanWithdraw(false);
        line.setFlowId("嘿嘿");
        node1.getOutLines().add(line);

        allnodes.add(node1);
        allnodes.add(node2);
        flow.setAllNodes(allnodes);
        flow.setStartNode(node1);
        Map<String,Object> argMap = new HashMap<String,Object>();
        argMap.put("allNodes",Node.class);
        argMap.put("allNodesInFlow",Node.class);
        Map<String,Node> beginNodeMap = new HashMap<String,Node>();
        beginNodeMap.put("fds",node2);
        flow.setNodeIdMap(beginNodeMap);
//        argMap.put("nodeIdMap",Map.class);

        String str = JSONObject.fromObject(flow).toString();
        System.out.println(str);
        JSONObject obj = JSONObject.fromObject(str);
//        Flow flow1 = (Flow)JSONObject.toBean(obj,Flow.class,argMap);
        JsonConfig jsonConfig = new JsonConfig();
        Map<String,Class> classMap = new HashMap<>();
        jsonConfig.setRootClass(Flow.class);
        classMap.put("allNodes",Node.class);
        classMap.put("allNodesInFlow",Node.class);
        classMap.put("nextNodes",Node.class);
        classMap.put("outLines",Line.class);

        jsonConfig.setClassMap(classMap);

//        jsonConfig.setNewBeanInstanceStrategy(new NewBeanInstanceStrategy() {
//            @Override
//            public Object newInstance(Class target, JSONObject source)
//                    throws InstantiationException, IllegalAccessException,
//                    SecurityException, NoSuchMethodException, InvocationTargetException {
//                System.out.println("target ============"+target.getName());
//                if( target != null ){
//                    if(target.getName().equals(Line.class.getName())){
//                        return new Line();
//                    }
//                    if(target.getName().equals(Map.class.getName())){
//                        return new HashMap();
//                    }
//                    return NewBeanInstanceStrategy.DEFAULT.newInstance(target, source);
//                }
//
//                return null;
//            }
//        });

        Flow flow1 = (Flow)JSONObject.toBean(obj,jsonConfig);
        System.out.println(flow1.getId());
        System.out.println(flow1.getFlowName());
        System.out.println(flow1.getFormKey());
        System.out.println(flow1.getStartNode().getId());
        for(Node testnode : flow1.getAllNodes()){
            System.out.println(testnode.getId());
            System.out.println(testnode.getSortNum());
            System.out.println(testnode.isBeginNode());
            System.out.println("---------------");
        }
        Map<String,Node> nodeIdMap = flow1.getNodeIdMap();
        Iterator<String> nodeIdIterator = nodeIdMap.keySet().iterator();
        MorpherRegistry morpherRegistry = new MorpherRegistry();
        Map<String,Node> outputMap = new HashMap<String,Node>();
        Morpher dynaMorpher = new BeanMorpher(Node.class,morpherRegistry,true);
        morpherRegistry.registerMorpher(dynaMorpher);
        while(nodeIdIterator.hasNext()){
            String nodeIdtmp = nodeIdIterator.next();
            Object tempObj = nodeIdMap.get(nodeIdtmp);
            outputMap.put(nodeIdtmp,(Node)morpherRegistry.morph(Node.class, tempObj));

//            System.out.println(tempObj.getClass().getName());
//            MorphDynaBean dynaBean = (MorphDynaBean)tempObj;
//            System.out.println(nodeIdMap.get(nodeIdtmp).getId());
        }
        flow1.setNodeIdMap(outputMap);
        System.out.println("==============================================");
        Iterator<String> finalIterator = flow1.getNodeIdMap().keySet().iterator();
        while(finalIterator.hasNext()){
            String key = finalIterator.next();
            if("true".equals(flow1.getNodeIdMap().get(key).getBeginNodeStr())){
                flow1.getNodeIdMap().get(key).setBeginNode(true);
            }
            System.out.println(flow1.getNodeIdMap().get(key).getId());
            System.out.println(flow1.getNodeIdMap().get(key).getSortNum());
            System.out.println(flow1.getNodeIdMap().get(key).isBeginNode());
        }
        System.out.println(flow1.getBussColumns());
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
        for(Node tempNode : flow1.getAllNodes()){
            List<Node> nextNodesListTmp = tempNode.getNextNodes();
            List<Line> nextLinesListTmp = tempNode.getOutLines();
            if(nextNodesListTmp != null && nextNodesListTmp.size() > 0){
                for(Node nn : nextNodesListTmp){
                    System.out.println(nn.getId()+" --- "+nn.getSortNum()+" -- "+nn.isBeginNode());
                }
            }
            if(nextLinesListTmp != null && nextLinesListTmp.size() > 0){
                for(Line ll : nextLinesListTmp){
                    System.out.println(ll.getFlowId()+" --- "+ll.isCanRetreat()+" -- "+ll.isCanWithdraw());
                }
            }
        }
    }
}

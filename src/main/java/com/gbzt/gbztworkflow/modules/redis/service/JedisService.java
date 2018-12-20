package com.gbzt.gbztworkflow.modules.redis.service;

import com.gbzt.gbztworkflow.modules.base.BaseService;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.*;
import com.gbzt.gbztworkflow.modules.redis.exception.JedisRuntimeException;
import com.gbzt.gbztworkflow.modules.redis.pool.JedisTool;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.HistProc;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.ProcInst;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.util.*;

@Service
public class JedisService extends BaseService {

    // 这里只是声明键的名称，而真正的保存方式可能是由SimpleCache提供，也有可能是由该类提供
    /** seperation: flowid*/
    public static final String CACHE_KEY_PREFIX_FLOW_DETAIL = "cache_flow_defination_detail_";
    /** seperation: nodeid*/
    public static final String CACHE_KEY_PREFIX_USER_NODE_PRIV="cache_user_node_priv_";

    private Logger logger = Logger.getLogger(JedisService.class);
    private static String STATIC_BEGIN_IDX = "0";

    private static String CONSTRUCTOR_TYPE_STRING = "string";
    private static String CONSTRUCTOR_TYPE_SET = "set";
    private static String CONSTRUCTOR_TYPE_ZSET = "zset";
    private static String CONSTRUCTOR_TYPE_HASH = "hash";

    //ns:none,key:[flowid]![flowname],type:zset
    private static String KEY_ALL_FLOWS = "flowAll:";
    private static String KEY_FLOW = "flow:";
    //ns:[flowid],key:[flowbussid],type:set
    private static String KEY_FLOW_FLOWBUSS = "flow!flowbuss:";
    private static String KEY_FLOWBUSS = "flowbuss:";

    //ns:[nodeid],key:hash key,type:hash
    private static String KEY_NODE = "node:";
    //ns:[lineid],key:hash key,type:hash
    private static String KEY_LINE = "line:";
    //ns:none,key:[flowid]![nodeid]![lineid],type:hash (hash value:nodeid or endnodeid)!important
    private static String KEY_FLOW_NODE_LINE = "flow!node!line:";

    //ns:[nodeid],key:[usernodeprivid],type:set ---- //special:only one element in this set.
    private static String KEY_NODE_USERNODEPRIV = "node!usernodepriv:";

    //ns:[usernodeprivid],key:hash,type:hash
    private static String KEY_USERNODEPRIV = "usernodepriv:";


    //ns:none,key:[procinstid]![flowid],type:zset(time score)
    private static String KEY_ALL_PROCINST = "procinstAll:";
    //ns:[procinstid],key:hash key,type:hash
    private static String KEY_PROCINST = "procinst:";
    //ns:[procinstid],key:[taskid]![parenttaskid],type:zset(time score)
    private static String KEY_PROCINST_TASK = "procinst!task:";
    //ns:[taskid],key:hash,type:hash
    private static String KEY_TASK = "task:";
    //ns:[procinstid],key:[histprocid]![taskid]![userid],type:zset(time score)
    private static String KEY_PROCINST_HISTPROC = "procinst!histproc:";
    //ns:[histprocid],key:hash,type:hash
    private static String KEY_HISTPROC = "histproc:";
    //+++++++++++++++++++++ flow related --begin
    /***
     * @interface
     * @specification flows:[z,nn]
     */
    public Integer countFlowByFlowName(String name){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            List<Object> scanResultList = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_ZSET,KEY_ALL_FLOWS,
                    "*!"+name);
            if(isBlank(scanResultList)){
                return 0;
            }
            for(Object scanResult : scanResultList){
                String[] scanArr = (String[])scanResult;
                String flowName = (scanArr[0].split("!"))[1];
                if(flowName.equals(name)){
                    return 1;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
        return 0;
    }


    /***
     * @interface
     * @specification flow-flowbuss:?[s,in] flowbuss:?[h,in]
     */
    public List<FlowBuss> findFlowBussByFlowId(String flowId){
        return internalFindAllSubElements(true , KEY_FLOW_FLOWBUSS + flowId , null ,
                CONSTRUCTOR_TYPE_SET , KEY_FLOWBUSS , new FlowBuss());
    }

    /***
     * @interface
     * @specification flow-flowbuss:?[s,in] flowbuss:?[h,in] flowAll:(z,nn) flow:?(h,in)
     */
    public void saveFlow(Flow flow){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            String flowId = flow.getId();
            Set<String> flowbussIds = jedisClient.smembers(KEY_FLOW_FLOWBUSS + flowId);
            List<Flow> currentFlows= internalFindAllSubElements(false,KEY_ALL_FLOWS,
                    flowId+"!*",CONSTRUCTOR_TYPE_ZSET,KEY_FLOW,new Flow());
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();

            if(isNotBlank(flowbussIds)) {
                pipeline.del(KEY_FLOW_FLOWBUSS + flowId);
                for (String flowbussid : flowbussIds) {
                    pipeline.del(KEY_FLOWBUSS + flowbussid);
                }
            }
            if(isNotBlank(currentFlows)){
                Flow oldFlow = currentFlows.get(0);
                pipeline.del(KEY_FLOW + flowId);
                pipeline.zrem(KEY_ALL_FLOWS,flowId+"!"+oldFlow.getFlowName());
            }
            Map<String,String> flowMap = CommonUtils.redisConvert(flow);
            pipeline.hmset(KEY_FLOW+flowId,flowMap);
            pipeline.zadd(KEY_ALL_FLOWS, CommonUtils.getCurrentTimeMillsDouble(),
                    flowId+"!"+flow.getFlowName());

            for(String column : flow.getBussColumns()){
                FlowBuss flowBuss = new FlowBuss();
                flowBuss.setId(CommonUtils.genUUid());
                flowBuss.setFlowId(flow.getId());
                flowBuss.setColumnName(column);
                flowBuss.genBaseVariables();
                Map<String,String> flowBussMap = CommonUtils.redisConvert(flowBuss);
                pipeline.hmset(KEY_FLOWBUSS + flowBuss.getId() , flowBussMap);
                pipeline.sadd(KEY_FLOW_FLOWBUSS + flowId , flowBuss.getId());
            }

            pipeline.exec();
            pipeline.sync();
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }
    /***
     * @interface
     * @specification flowAll:(z)
     */
    public Flow findFlowByIdOrName(String flowId,String flowName){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            if(isNotBlank(flowId)){
                List<Object> resultList = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_ZSET,KEY_ALL_FLOWS,
                        flowId+"!*");
                if(isBlank(resultList)){
                    return null;
                }
                for(Object tuple : resultList) {
                    String[] tupleArr = (String[]) tuple;
                    String tmpFlowId = (tupleArr[0].split("!"))[0];
                    if (tmpFlowId.equals(flowId)) {
                        Map<String, String> flowMap = jedisClient.hgetAll(KEY_FLOW + tmpFlowId);
                        if (isBlank(flowMap)) {
                            return null;
                        }
                        Flow flow = new Flow();
                        flow = CommonUtils.redisConvert(flow, flowMap);
                        return flow;
                    }
                }
            }else{
                List<Object> resultList = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_ZSET,KEY_ALL_FLOWS,
                        "*!"+flowName);
                if(isBlank(resultList)){
                    return null;
                }
                for(Object tuple : resultList) {
                    String[] tupleArr = (String[]) tuple;
                    String tmpFlowId = (tupleArr[0].split("!"))[0];
                    String tmpFlowName = (tupleArr[0].split("!"))[1];
                    if (tmpFlowName.equals(flowName)) {
                        Map<String, String> flowMap = jedisClient.hgetAll(KEY_FLOW + tmpFlowId);
                        if (isBlank(flowMap)) {
                            return null;
                        }
                        Flow flow = new Flow();
                        flow = CommonUtils.redisConvert(flow, flowMap);
                        return flow;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
        return null;
    }

    /***
     * @interface
     * @specification flowAll:(z,nn) flow:?(h,in)
     */
    public List<Flow> findFlowsByDelTag(boolean delTag){
        Jedis jedisClient = JedisTool.getJedis();
        List<Flow> flows = new ArrayList<Flow>();
        try{
            Set<String> flowInfoSet = jedisClient.zrevrange(KEY_ALL_FLOWS , 0,new Long(Integer.MAX_VALUE));
            for(String flowInfo : flowInfoSet){
                String flowid = flowInfo.split("!")[0];
                Map<String,String> flowMap = jedisClient.hgetAll(KEY_FLOW + flowid);
                if(isBlank(flowMap)){
                    continue;
                }
                Flow flow = new Flow();
                flow = CommonUtils.redisConvert(flow,flowMap);
                if(delTag != flow.isDelTag()){
                    continue;
                }
                flows.add(flow);
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
        return flows;
    }

    public void delFlow(String flowId){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            List<Object> flowScanResultList = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_ZSET,KEY_ALL_FLOWS,
                    flowId+"!*");
            List<Object> flowNLResultList = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_HASH,KEY_FLOW_NODE_LINE,
                    flowId+"!*");
            if(isBlank(flowScanResultList)){
                return ;
            }
            String[] flowEleArr = (((String[])flowScanResultList.get(0))[0]).split("!");
            internalClearParentAndSubElements(KEY_ALL_FLOWS,flowId+"!"+flowEleArr[1],CONSTRUCTOR_TYPE_ZSET,KEY_FLOW);
            internalClearAllParentAndSubElements(KEY_FLOW_FLOWBUSS + flowId,CONSTRUCTOR_TYPE_SET,KEY_FLOWBUSS);
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();
            for(Object flowNLResult : flowNLResultList){
                String flowNLHashKey = ((String[])flowNLResult)[0];
                String[] flowNLHashArray = flowNLHashKey.split("!");
                if(flowNLHashArray.length == 2){
                    pipeline.del(KEY_NODE + flowNLHashArray[1]);
                }else if(flowNLHashArray.length == 3){
                    pipeline.del(KEY_LINE + flowNLHashArray[2]);
                }else{
                    continue;
                }
                pipeline.hdel(KEY_FLOW_NODE_LINE , flowNLHashKey);
            }
            pipeline.exec();
            pipeline.sync();
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    //--------------------- flow related --end

    //+++++++++++++++++++++ node and line related --begin

    public List<Node> findNodeByFlowIdOrderByDefIdDesc(String flowId){
        Jedis jedisClient = JedisTool.getJedis();
        List<Node> nodes = new ArrayList<Node>();
        try{
            List<Object> nodeOriList = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_HASH,
                    KEY_FLOW_NODE_LINE,flowId+"!*");
            if(isBlank(nodeOriList)){
                return nodes;
            }
            Pipeline pipeline = jedisClient.pipelined();
            for(Object obj : nodeOriList){
                String[] keyArr = ((String[])obj)[0].split("!");
                if(keyArr.length == 3){
                    continue;
                }
                //pre handle...
                Node node = new Node();
                nodes.add(node);
                pipeline.hgetAll(KEY_NODE + keyArr[1]);
            }
            List<Object> resultList = pipeline.syncAndReturnAll();
            if(isBlank(resultList)){
                return new ArrayList<Node>();
            }
            for(int i=0;i<resultList.size();i++){
                Node currentNode = nodes.get(i);
                Map<String,String> objMap = (Map<String,String>)resultList.get(i);
                CommonUtils.redisConvert(currentNode,objMap);
            }
            return nodes;
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public Node findNodeById(String nodeId){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Map<String,String> nodeMap = jedisClient.hgetAll(KEY_NODE + nodeId);
            if(isBlank(nodeMap)){
                return null;
            }
            Node node = new Node();
            CommonUtils.redisConvert(node,nodeMap);
            return node;
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public List<Node> findNodeByIds(List<String> nodeIds){
        Jedis jedisClient = JedisTool.getJedis();
        List<Node> nodes = new ArrayList<Node>();
        List<Node> finalNodes = new ArrayList<Node>();
        try{
            Pipeline pipeline = jedisClient.pipelined();
            for(String nodeid : nodeIds){
                pipeline.hmget(KEY_NODE + nodeid);
                Node node = new Node();
                nodes.add(node);
            }
            List<Object> searchResultList = pipeline.syncAndReturnAll();
            for(int i=0;i<searchResultList.size();i++){
                Object searchResult = searchResultList.get(i);
                Node node = nodes.get(i);
                Map<String,String> nodemap = (Map<String,String>)searchResult;
                if(isBlank(nodemap)){
                    continue;
                }
                CommonUtils.redisConvert(node,nodemap);
            }
            for(Node node : nodes){
                if(isNotBlank(node.getId())){
                    finalNodes.add(node);
                }
            }
            return finalNodes;
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    // outlines:can be scaned by key,but inlines can not,must scan whole hash
    public void findAndSetNodeWholeAttributes(Node targetNode){
        Jedis jedisClient = JedisTool.getJedis();
        String flowId = targetNode.getFlowId();
        try{
            List<String[]> scanAllList = new ArrayList<String[]>();
            Set<String> outLineIds = new HashSet<String>();
            Set<String> inLineIds  = new HashSet<String>();
            Set<String> outNodeIds  = new HashSet<String>();
            Set<String> inNodeIds  = new HashSet<String>();
            List<Line> outLineList = new ArrayList<Line>();
            List<Line> inLineList = new ArrayList<Line>();
            List<Node> inNodeList = new ArrayList<Node>();
            List<Node> outNodeList = new ArrayList<Node>();
            List<Object> scanResultList = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_HASH,
                    KEY_FLOW_NODE_LINE,flowId+"!*");
            for(Object scanResult : scanResultList){
                String[] singleLineResult = (String[])scanResult;
                String[] scankeyarr = singleLineResult[0].split("!");
                if(scankeyarr.length == 2){
                    continue;
                }
                scanAllList.add(singleLineResult);
            }

            Pipeline pipeline = jedisClient.pipelined();
            for(String[] scanResult : scanAllList){
                String[] keyArr = scanResult[0].split("!");
                String value = scanResult[1];
                if(keyArr[1].equals(targetNode.getId())){
                    outLineIds.add(keyArr[2]);
                    outNodeIds.add(value);
                }
                if(value.equals(targetNode.getId())){
                    inLineIds.add(keyArr[2]);
                    inNodeIds.add(keyArr[1]);
                }
            }

            for(String outLineId : outLineIds){
                pipeline.hgetAll(KEY_LINE + outLineId);
            }
            List<Object> execResultList = pipeline.syncAndReturnAll();
            for(Object execResult : execResultList){
                Map<String,String> attrMap = (Map<String,String>)execResult;
                if(isBlank(attrMap)){
                    continue;
                }
                Line outLine = new Line();
                CommonUtils.redisConvert(outLine,attrMap);
                outLineList.add(outLine);
            }
            pipeline = jedisClient.pipelined();
            for(String inLineId : inLineIds){
                pipeline.hgetAll(KEY_LINE + inLineId);
            }
            execResultList = pipeline.syncAndReturnAll();
            for(Object execResult : execResultList){
                Map<String,String> attrMap = (Map<String,String>)execResult;
                if(isBlank(attrMap)){
                    continue;
                }
                Line inLine = new Line();
                CommonUtils.redisConvert(inLine,attrMap);
                inLineList.add(inLine);
            }
            pipeline = jedisClient.pipelined();
            for(String outNodeId : outNodeIds){
                pipeline.hgetAll(KEY_NODE + outNodeId);
            }
            execResultList = pipeline.syncAndReturnAll();
            for(Object execResult : execResultList){
                Map<String,String> attrMap = (Map<String,String>)execResult;
                if(isBlank(attrMap)){
                    continue;
                }
                Node outNode = new Node();
                CommonUtils.redisConvert(outNode,attrMap);
                outNodeList.add(outNode);
            }
            pipeline = jedisClient.pipelined();
            for(String inNodeId : inNodeIds){
                pipeline.hgetAll(KEY_NODE + inNodeId);
            }
            execResultList = pipeline.syncAndReturnAll();
            for(Object execResult : execResultList){
                Map<String,String> attrMap = (Map<String,String>)execResult;
                if(isBlank(attrMap)){
                    continue;
                }
                Node inNode = new Node();
                CommonUtils.redisConvert(inNode,attrMap);
                inNodeList.add(inNode);
            }

            targetNode.setOutLines(outLineList);
            targetNode.setInLines(inLineList);
            targetNode.setNextNodes(outNodeList);
            targetNode.setForeNodes(inNodeList);

        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public void saveNode(Node targetNode){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();
            Map<String,String> nodeMap = CommonUtils.redisConvert(targetNode);
            pipeline.del(KEY_NODE + targetNode.getId());
            pipeline.hmset(KEY_NODE + targetNode.getId(),nodeMap);
            pipeline.hdel(KEY_FLOW_NODE_LINE,targetNode.getFlowId()+"!"+targetNode.getId());
            pipeline.hset(KEY_FLOW_NODE_LINE,targetNode.getFlowId()+"!"+targetNode.getId(),targetNode.getId());
            pipeline.exec();
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public Integer counterNodeNameByFlowId(String flowId,String nodeName){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            List<Object> nodesInFlow = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_HASH,KEY_FLOW_NODE_LINE,
                    flowId+"!*");
            List<String> nodeIdList = new ArrayList<String>();
            for(Object nodeObj : nodesInFlow){
                String hashkey = ((String[])nodeObj)[0];
                String[] hashKeyArr = hashkey.split("!");
                if(hashKeyArr.length == 3){
                    continue;
                }
                nodeIdList.add(hashKeyArr[1]);
            }
            Pipeline pipeline = jedisClient.pipelined();
            for(String nodeId : nodeIdList){
                pipeline.hgetAll(KEY_NODE + nodeId);
            }
            List<Object> searchList = pipeline.syncAndReturnAll();
            for(Object searchObj : searchList){
                Map<String,String> tmpNodeMap = (Map<String,String>)searchObj;
                if(nodeName.equals(tmpNodeMap.get("name"))){
                    return 1;
                }
            }
            return 0;
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public void delNode(Node node){
        Jedis jedisClient = JedisTool.getJedis();
        String flowId = node.getFlowId();
        try{
            List<String[]> scanAllList = new ArrayList<String[]>();
            List<String> delKeys = new ArrayList<String>();
            Set<String> outLineIds = new HashSet<String>();
            Set<String> inLineIds  = new HashSet<String>();
            List<Object> scanResultList = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_HASH,
                    KEY_FLOW_NODE_LINE,flowId+"!*");
            for(Object scanResult : scanResultList){
                String[] singleLineResult = (String[])scanResult;
                String[] scankeyarr = singleLineResult[0].split("!");
                if(node.getId().equals(scankeyarr[1])){
                    // first place setting del keys,need complete key.
                    delKeys.add(singleLineResult[0]);
                }
                if(scankeyarr.length == 2){
                    continue;
                }
                scanAllList.add(singleLineResult);
            }

            Pipeline pipeline = jedisClient.pipelined();
            for(String[] scanResult : scanAllList){
                String[] keyArr = scanResult[0].split("!");
                String value = scanResult[1];
                if(keyArr[1].equals(node.getId())){
                    outLineIds.add(keyArr[2]);
                }
                if(value.equals(node.getId())){
                    inLineIds.add(keyArr[2]);
                    // second place setting del keys,need complete key.
                    delKeys.add(scanResult[0]);
                }
            }
            pipeline.multi();
            pipeline.del(KEY_NODE + node.getId());
            for(String delKey : delKeys){
                pipeline.hdel(KEY_FLOW_NODE_LINE , delKey);
            }
            for(String inLineId : inLineIds){
                pipeline.del(KEY_LINE + inLineId);
            }
            for(String outLineId : outLineIds){
                pipeline.del(KEY_LINE + outLineId);
            }
            pipeline.exec();
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }


    public Line findLineByBeginNodeIdAndEndNodeId(String beginNodeId,String endNodeId){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            List<Object> scanResult = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_HASH,KEY_FLOW_NODE_LINE,
                    "*!"+beginNodeId+"!*");
            if(isBlank(scanResult)){
                return null;
            }
            for(Object hashRow : scanResult){
                String hashKey = ((String[])hashRow)[0];
                String[] hashKeyArr = hashKey.split("!");
                String hashValue = ((String[])hashRow)[1];
                if(endNodeId.equals(hashValue)){
                    String lineId = hashKeyArr[2];
                    Line line = new Line();
                    Map<String,String> lineMap = jedisClient.hgetAll(KEY_LINE + lineId);
                    if(isBlank(lineMap)){
                        return null;
                    }
                    CommonUtils.redisConvert(line,lineMap);
                    return line;
                }
            }
            return null;
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    //[flowid]![beginnodeid]![lineid] ---- [endnodeid]
    public void saveLine(Line line){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Line currentLine = findLineByBeginNodeIdAndEndNodeId(line.getBeginNodeId(),line.getEndNodeId());
            Map<String,String> lineMap = CommonUtils.redisConvert(line);
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();
            pipeline.del(KEY_LINE + line.getId());
            pipeline.hmset(KEY_LINE + line.getId(),lineMap);
            if(currentLine != null){
                pipeline.hdel(KEY_FLOW_NODE_LINE , line.getFlowId(),line.getBeginNodeId(),currentLine.getId());
            }
            pipeline.hdel(KEY_FLOW_NODE_LINE,line.getFlowId() + line.getBeginNodeId() + line.getId());
            pipeline.hset(KEY_FLOW_NODE_LINE,line.getFlowId() + line.getBeginNodeId() + line.getId(),
                    line.getEndNodeId());
            pipeline.exec();
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public void delLine(Line line){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();
            pipeline.del(KEY_LINE + line.getId());
            pipeline.hdel(KEY_FLOW_NODE_LINE , line.getFlowId()+"!"+line.getBeginNodeId()+"!"+line.getId());
            pipeline.exec();
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    //--------------------- node and line related --end


    public String findCachedString(String key){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            return jedisClient.get(key);
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public void setCachedString(String key,String value){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            if(value == null){
                jedisClient.del(key);
                return ;
            }
            jedisClient.set(key,value);
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }


    public UserNodePriv findUserNodePrivByNodeId(String nodeId){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            List<UserNodePriv> userNodePrivs = internalFindAllSubElements(true,KEY_NODE_USERNODEPRIV + nodeId,
                    null,CONSTRUCTOR_TYPE_SET,KEY_USERNODEPRIV,new UserNodePriv());
            return userNodePrivs.get(0);
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public void saveUserNodePrivs(UserNodePriv userNodePriv){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            internalClearParentAndSubElements(KEY_NODE_USERNODEPRIV + userNodePriv.getNodeId(),
                    userNodePriv.getId(),CONSTRUCTOR_TYPE_SET,KEY_USERNODEPRIV);
            internalAddParentAndSubElements(KEY_NODE_USERNODEPRIV + userNodePriv.getNodeId(),
                    new String[]{userNodePriv.getId(),null},CONSTRUCTOR_TYPE_SET,
                    KEY_USERNODEPRIV+userNodePriv.getId(),userNodePriv);
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public void delUserNodePrivs(UserNodePriv userNodePriv){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            internalClearParentAndSubElements(KEY_NODE_USERNODEPRIV + userNodePriv.getNodeId(),
                    userNodePriv.getId(),CONSTRUCTOR_TYPE_SET,KEY_USERNODEPRIV);
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    //+++++++++++++++++++++ flow runtime related --begin
    public ProcInst findProcInstById(String procInstId){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            List<ProcInst> procInsts = internalFindAllSubElements(false,KEY_ALL_PROCINST,
                    procInstId+"!*",CONSTRUCTOR_TYPE_ZSET,KEY_PROCINST,new ProcInst());
            if(isBlank(procInsts)){
                return null;
            }
            return procInsts.get(0);
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public void saveProcInst(ProcInst procInst){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            internalAddParentAndSubElements(KEY_ALL_PROCINST,
                    new String[]{procInst.getId()+"!"+procInst.getFlowId(),CommonUtils.getCurrentTimeMillsString()},
                    CONSTRUCTOR_TYPE_ZSET,KEY_PROCINST+procInst.getId(),procInst);
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public Task findTaskById(String taskId){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Map<String,String> taskMap = jedisClient.hgetAll(KEY_TASK + taskId);
            if(isBlank(taskMap)){
                return null;
            }
            Task task = new Task();
            CommonUtils.redisConvert(task,taskMap);
            return task;
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public List<Task> findSubTaskByTaskId(String procInstId,String taskId){
        Jedis jedisClient = JedisTool.getJedis();
        List<Task> tasks = new ArrayList<Task>();
        try{
            List<Object> scanResult = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_ZSET,
                    KEY_PROCINST_TASK + procInstId,"*!"+taskId);
            if(isBlank(scanResult)){
                return tasks;
            }
            Pipeline pipeline = jedisClient.pipelined();
            for(Object obj : scanResult){
                String[] taskRow = (String[])obj;
                String[] keyArr = (taskRow[0]).split("!");
                if(keyArr.length != 2){
                    continue;
                }
                pipeline.hgetAll(KEY_TASK + keyArr[0]);
            }
            List<Object> taskResultList = pipeline.syncAndReturnAll();
            for(Object taskResult : taskResultList){
                Map<String,String> taskMap = (Map<String,String>)taskResult;
                if(isBlank(taskMap)){
                    continue;
                }
                Task finaltask = new Task();
                CommonUtils.redisConvert(finaltask,taskMap);
                tasks.add(finaltask);
            }
            return tasks;
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public void saveTask(List<Task> tasks){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();
            for(Task task : tasks){
                String parentTaskId = task.getParentTaskId();
                if(StringUtils.isBlank(parentTaskId)){
                    parentTaskId = "[empty]";
                }
                internalAddParentAndSubElements(pipeline,KEY_PROCINST_TASK + task.getProcInstId(),
                        new String[]{task.getId()+"!"+parentTaskId,CommonUtils.getCurrentTimeMillsString()},
                        CONSTRUCTOR_TYPE_ZSET,KEY_TASK + task.getId(),task);
            }
            pipeline.exec();
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public void saveTask(Task task){
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(task);
        saveTask(tasks);
    }

    public void updateTask(Task task){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();
            pipeline.del(KEY_TASK + task.getId());
            Map<String,String> taskMap = CommonUtils.redisConvert(task);
            pipeline.hmset(KEY_TASK + task.getId() , taskMap);
            pipeline.exec();
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    public void saveHistProc(HistProc histProc){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();
            internalAddParentAndSubElements(pipeline,KEY_PROCINST_HISTPROC+histProc.getProcInstId(),
                    new String[]{histProc.getId()+"!"+histProc.getTaskId()+"!"+histProc.getUserId(),CommonUtils.getCurrentTimeMillsString()},
                    CONSTRUCTOR_TYPE_ZSET,KEY_HISTPROC + histProc.getId(),histProc);
            pipeline.exec();
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    //--------------------- flow runtime related --begin



    //#############################################################################################################################
    //#############################################################################################################################
    //#############################################################################################################################
    //#############################################################################################################################
    //#############################################################################################################################
    //#############################################################################################################################
    //#############################################################################################################################
    //#############################################################################################################################
    //#############################################################################################################################


    // only support "equals" situation
    // two usages:
    // 1. get all data ---- datatag=true , despreate parentValue , child hash part use parent search result
    // 2. get one data(filter) ---- datatag=false , compare parentValue , child hash part use parentValue
    // (attention: only support equal situation! quote parentElement belows.)
        private <T> List<T> internalFindAllSubElements(Jedis jedisClient,boolean datatag,String parentKey,String parentValue,
                                                   String constructorType,String childKey,T t){
        List<T> resultList = new ArrayList<T>();
        if(!constructorType.equals(CONSTRUCTOR_TYPE_ZSET) && !constructorType.equals(CONSTRUCTOR_TYPE_SET)){
            return resultList;
        }
        if(datatag){
            List<Object> parentScanResult = null;
            if(constructorType.equals(CONSTRUCTOR_TYPE_ZSET)){
                parentScanResult = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_ZSET,parentKey,"*");
            }
            if(constructorType.equals(CONSTRUCTOR_TYPE_SET)){
                parentScanResult = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_SET,parentKey,"*");
            }

            if(isBlank(parentScanResult)){
                return resultList;
            }
            for(Object parentElement : parentScanResult){
                String subKey = ((String[])parentElement)[0];
                // [!!] use subKey as sub key part.
                Map<String,String> subMap = jedisClient.hgetAll(childKey + subKey);
                if(isBlank(subMap)){
                    continue;
                }
                t = CommonUtils.redisConvert(t,subMap);
                resultList.add(t);
            }
        }else{

            List<Object> parentScanResult = internalScanValueInConstructor(jedisClient,constructorType,parentKey,parentValue);
            if(isBlank(parentScanResult)){
                return resultList;
            }
            // [!!] use parentKey as sub key part.
            Map<String,String> tMap = jedisClient.hgetAll(childKey + parentValue);
            t = CommonUtils.redisConvert(t , tMap);
            resultList.add(t);
        }
        return resultList;
    }
    private <T> List<T> internalFindAllSubElements(boolean datatag,String parentKey,String parentValue,
                                                   String constructorType,String childKey,T t){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            return internalFindAllSubElements(jedisClient,datatag,parentKey,parentValue,constructorType,childKey,t);
        }catch(Exception e){
            e.printStackTrace();;
            throw new JedisRuntimeException(e);
        }finally{
            JedisTool.returnJedis(jedisClient);
        }
    }


    // parentKey can be in or nn , childType must be in
    private void internalClearParentAndSubElements(Pipeline pipeline,String parentKey,String parentValue,
                                                   String constructorType,String childKey){
        if(constructorType.equals(CONSTRUCTOR_TYPE_SET)){
            pipeline.srem(parentKey,parentValue);
        }else if(constructorType.equals(CONSTRUCTOR_TYPE_ZSET)){
            pipeline.zrem(parentKey,parentValue);
        }
        pipeline.del(childKey + parentValue);
    }
    private void internalClearParentAndSubElements(String parentKey,String parentValue,
                                                   String constructorType,String childKey){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();
            internalClearParentAndSubElements(pipeline,parentKey,parentValue,constructorType,childKey);
            pipeline.exec();
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    private void internalClearAllParentAndSubElements(String parentKey,String constructorType,String childKey){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Set<String> parentKeySets = null;
            if(constructorType.equals(CONSTRUCTOR_TYPE_SET)){
                parentKeySets = jedisClient.smembers(parentKey);
            }else if(constructorType.equals(CONSTRUCTOR_TYPE_ZSET)){
                parentKeySets = jedisClient.zrange(parentKey,0,Integer.MAX_VALUE);
            }
            if(isBlank(parentKeySets)){
                return ;
            }
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();
            pipeline.del(parentKey);
            for(String childKeyValue : parentKeySets){
                pipeline.del(childKey + childKeyValue);
            }
            pipeline.exec();
            pipeline.sync();
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }


    // parentKey can be in or nn , childType must be in
    private <T> void internalAddParentAndSubElements(Pipeline pipeline,String parentKey,String[] parentValue,
                                                     String constructorType,String childKey,T t){
        if(constructorType.equals(CONSTRUCTOR_TYPE_SET)){
            pipeline.sadd(parentKey,parentValue[0]);
        }else if(constructorType.equals(CONSTRUCTOR_TYPE_ZSET)){
            pipeline.zadd(parentKey,Double.parseDouble(parentValue[1]),parentValue[0]);
        }
        pipeline.del(childKey);
        Map<String,String> childMap = CommonUtils.redisConvert(t);
        pipeline.hmset(childKey,childMap);
    }

    private <T> void internalAddParentAndSubElements(String parentKey,String[] parentValue,
                                                     String constructorType,String childKey,T t){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();
            internalAddParentAndSubElements(pipeline,parentKey,parentValue,constructorType,childKey,t);
            pipeline.exec();
        }catch(Exception e){
            e.printStackTrace();
            throw new JedisRuntimeException(e);
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }


    private List<Object> internalScanValueInConstructor(Jedis jedis,String constructorType,String key,String pattern){
        List<Object> resultList = new ArrayList<Object>();
        ScanParams params= new ScanParams();
        params.match(pattern);
        if(constructorType.equals(CONSTRUCTOR_TYPE_STRING)){
            ScanResult<String> result = jedis.scan(STATIC_BEGIN_IDX,params);
            resultList.addAll(result.getResult());
            while(!STATIC_BEGIN_IDX.equals(result.getStringCursor())){
                result = jedis.scan(result.getStringCursor(),params);
                resultList.addAll(result.getResult());
            }
            return resultList;
        }else if(constructorType.equals(CONSTRUCTOR_TYPE_SET)){
            ScanResult<String> result = jedis.sscan(key,STATIC_BEGIN_IDX,params);
            resultList.addAll(result.getResult());
            while(!STATIC_BEGIN_IDX.equals(result.getStringCursor())){
                result = jedis.sscan(key,result.getStringCursor(),params);
                resultList.addAll(result.getResult());
            }
            return resultList;
        }else if(constructorType.equals(CONSTRUCTOR_TYPE_HASH)){
            ScanResult<Map.Entry<String,String>> result = jedis.hscan(key,STATIC_BEGIN_IDX,params);
            for(Map.Entry<String,String> entry : result.getResult()){
                String[] entryArr = new String[]{entry.getKey(),entry.getValue()};
                resultList.add(entryArr);
            }
            while(!STATIC_BEGIN_IDX.equals(result.getStringCursor())){
                result = jedis.hscan(key,result.getStringCursor(),params);
                for(Map.Entry<String,String> entry : result.getResult()){
                    String[] entryArr = new String[]{entry.getKey(),entry.getValue()};
                    resultList.add(entryArr);
                }
            }
            return resultList;
        }else if(constructorType.equals(CONSTRUCTOR_TYPE_ZSET)){
            ScanResult<Tuple> result = jedis.zscan(key,STATIC_BEGIN_IDX,params);
            for(Tuple tuple : result.getResult()){
                String[] tupleArr = new String[]{tuple.getElement(),String.valueOf(tuple.getScore())};
                resultList.add(tupleArr);
            }
            while(!STATIC_BEGIN_IDX.equals(result.getStringCursor())){
                result = jedis.zscan(key,result.getStringCursor(),params);
                for(Tuple tuple : result.getResult()){
                    String[] tupleArr = new String[]{tuple.getElement(),String.valueOf(tuple.getScore())};
                    resultList.add(tupleArr);
                }
            }
            return resultList;
        }
        return null;
    }

    public static void main(String[] args) {
        List<String> resultList =new ArrayList<String>();
        String result = "";
        for(int i=0;i<20;i++){
            result = "result "+(i+1);
            resultList.add(result);
        }
        System.out.println(resultList);
    }
}

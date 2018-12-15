package com.gbzt.gbztworkflow.modules.redis.service;

import com.gbzt.gbztworkflow.modules.base.BaseService;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.FlowBuss;
import com.gbzt.gbztworkflow.modules.redis.exception.JedisRuntimeException;
import com.gbzt.gbztworkflow.modules.redis.pool.JedisTool;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class JedisService extends BaseService {

    private Logger logger = Logger.getLogger(JedisService.class);
    private static String STATIC_BEGIN_IDX = "0";

    private static String CONSTRUCTOR_TYPE_STRING = "string";
    private static String CONSTRUCTOR_TYPE_SET = "set";
    private static String CONSTRUCTOR_TYPE_ZSET = "zset";
    private static String CONSTRUCTOR_TYPE_HASH = "hash";

    private static String KEY_ALL_FLOWS = "flowAll:";
    private static String KEY_FLOW = "flow:";
    private static String KEY_FLOW_FLOWBUSS = "flow!flowbuss:";
    private static String KEY_FLOWBUSS = "flowbuss:";

    private static String KEY_ALL_PROCINST = "proinstAll:";fds
//    private static String

    //--------------------- flow related --begin
    /***
     * @interface
     * @specification flows:[z,nn]
     */
    public Integer countFlowByFlowName(String name){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            List<Object> scanResultList = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_ZSET,KEY_ALL_FLOWS,"*!"+name);
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
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();

            if(isNotBlank(flowbussIds)) {
                pipeline.del(KEY_FLOW_FLOWBUSS + flowId);
                for (String flowbussid : flowbussIds) {
                    pipeline.del(KEY_FLOWBUSS + flowbussid);
                }
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
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
        return flows;
    }

    //--------------------- flow related --end


    // only support "equals" situation
    // two usages:
    // 1. get all data ---- datatag=true , despreate parentValue , child hash part use parent search result
    // 2. get one data(filter) ---- datatag=false , compare parentValue , child hash part use parentValue
    // (attention: only support equal situation! quote parentElement belows.)
    private <T> List<T> internalFindAllSubElements(boolean datatag,String parentKey,String parentValue,
                                                   String constructorType,String childKey,T t){
        Jedis jedisClient = JedisTool.getJedis();
        List<T> resultList = new ArrayList<T>();
        try{
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
                if(constructorType.equals(CONSTRUCTOR_TYPE_ZSET)){
                    List<Object> parentScanResult = internalScanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_ZSET,parentKey,parentValue);
                    if(isBlank(parentScanResult)){
                        return resultList;
                    }
                }
                if(constructorType.equals(CONSTRUCTOR_TYPE_SET) && !jedisClient.sismember(parentKey,parentValue)){
                    return resultList;
                }
                // [!!] use parentKey as sub key part.
                Map<String,String> tMap = jedisClient.hgetAll(childKey + parentValue);
                t = CommonUtils.redisConvert(t , tMap);
                resultList.add(t);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
        return resultList;
    }


    // parentKey can be in or nn , childType must be in
    private void internalClearParentAndSubElements(String parentKey,String parentValue,
                                                   String constructorType,String childType){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();
            if(constructorType.equals(CONSTRUCTOR_TYPE_SET)){
                pipeline.srem(parentKey,parentValue);
            }else if(constructorType.equals(CONSTRUCTOR_TYPE_ZSET)){
                pipeline.zrem(parentKey,parentValue);
            }
            pipeline.del(childType + parentValue);
            pipeline.exec();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

    // parentKey can be in or nn , childType must be in
    private <T> void internalAddParentAndSubElements(String parentKey,String[] parentValue,
                                                     String constructorType,String childType,T t){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();
            if(constructorType.equals(CONSTRUCTOR_TYPE_SET)){
                pipeline.sadd(parentKey,parentValue[0]);
            }else if(constructorType.equals(CONSTRUCTOR_TYPE_ZSET)){
                pipeline.zadd(parentKey,Double.parseDouble(parentValue[1]),parentValue[0]);
            }
            pipeline.del(childType);
            Map<String,String> childMap = CommonUtils.redisConvert(t);
            pipeline.hmset(childType,childMap);
            pipeline.exec();
        }catch(Exception e){
            e.printStackTrace();
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
}

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
    private static String KEY_FLOW_FLOWBUSS = "flow-flowbuss:";
    private static String KEY_FLOWBUSS = "flowbuss:";

    /***
     * @interface
     * @specification flows:[z,nn]
     */
    public Integer countFlowByFlowName(String name){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            List<Object> scanResultList = scanValueInConstructor(jedisClient,CONSTRUCTOR_TYPE_ZSET,KEY_ALL_FLOWS,"*-"+name);
            if(isBlank(scanResultList)){
                return 0;
            }
            for(Object scanResult : scanResultList){
                String[] scanArr = (String[])scanResult;
                String flowName = (scanArr[0].split("-"))[1];
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
        Jedis jedisClient = JedisTool.getJedis();
        List<FlowBuss> flowBusses = new ArrayList<FlowBuss>();
        try{
            Set<String> flowbussIds = jedisClient.smembers(KEY_FLOW_FLOWBUSS + flowId);
            if(isBlank(flowbussIds)){
                return flowBusses;
            }
            for(String flowbussid : flowbussIds){
                Map<String,String> flowbussMap = jedisClient.hgetAll(KEY_FLOWBUSS + flowbussid);
                if(isBlank(flowbussMap)){
                    continue;
                }
                FlowBuss flowBuss = new FlowBuss();
                flowBuss = CommonUtils.redisConvert(flowBuss,flowbussMap);
                flowBusses.add(flowBuss);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
        return flowBusses;
    }

    /***
     * @interface
     * @specification flow-flowbuss:?[s,in] flowbuss:?[h,in]
     */
    public void clearFlowBussByFlowId(String flowId){
        Jedis jedisClient = JedisTool.getJedis();
        try{
            Set<String> flowbussIds = jedisClient.smembers(KEY_FLOW_FLOWBUSS + flowId);
            if(isBlank(flowbussIds)){
                return ;
            }
            Pipeline pipeline = jedisClient.pipelined();
            pipeline.multi();
            pipeline.del(KEY_FLOW_FLOWBUSS + flowId);
            for(String flowbussid : flowbussIds){
                pipeline.del(KEY_FLOWBUSS + flowbussid);
            }
            pipeline.exec();
            pipeline.sync();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            JedisTool.returnJedis(jedisClient);
        }
    }

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
//            pipeline.hmset(KEY_FLOW)
            List<FlowBuss> flowBusses = new ArrayList<FlowBuss>();
            for(String column : flow.getBussColumns()){
                FlowBuss flowBuss = new FlowBuss();
                flowBuss.setId(CommonUtils.genUUid());
                flowBuss.setFlowId(flow.getId());
                flowBuss.setColumnName(column);
                flowBuss.genBaseVariables();
                flowBusses.add(flowBuss);
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





    private List<Object> scanValueInConstructor(Jedis jedis,String constructorType,String key,String pattern){
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

package com.gbzt.gbztworkflow.modules.flowdefination.service;

import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowBussDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.FlowBuss;
import com.gbzt.gbztworkflow.modules.redis.service.JedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class FlowBussCacheService {

    @Autowired
    private JedisService jedisService;

    public List<FlowBuss> findAllByFlowId(String flowId){
        return jedisService.findFlowBussByFlowId(flowId);
    }

}

package com.gbzt.gbztworkflow.modules.flowdefination.service;

import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowBussDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.FlowBuss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlowBussService {
    @Autowired
    private FlowBussDao flowBussDao;

    public List<FlowBuss> findAllByFlowId(String flowId){
        return flowBussDao.findAllByFlowId(flowId);
    }
}

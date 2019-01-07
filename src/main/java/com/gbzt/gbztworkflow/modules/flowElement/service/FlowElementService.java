package com.gbzt.gbztworkflow.modules.flowElement.service;

import com.gbzt.gbztworkflow.modules.flowElement.dao.FlowElementDao;
import com.gbzt.gbztworkflow.modules.flowElement.entity.FlowElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FlowElementService{
    
    @Autowired
    private FlowElementDao flowElementDao;
    
    public List<FlowElement> findFlowElementsByFlowId(String flowId){
        return flowElementDao.findFlowElementsByFlowId(flowId);
    }
}

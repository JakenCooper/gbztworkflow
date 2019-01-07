package com.gbzt.gbztworkflow.modules.flowElement.dao;

import com.gbzt.gbztworkflow.modules.flowElement.entity.FlowElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FlowElementDao extends JpaRepository<FlowElement,Integer>, JpaSpecificationExecutor<FlowElement> {
    
    public FlowElement findFlowElementById(String id);
    
    public List<FlowElement>  findFlowElementsByFlowId(String id);
    @Transactional
    public Integer deleteFlowElementByFlowId(String flowId);

}

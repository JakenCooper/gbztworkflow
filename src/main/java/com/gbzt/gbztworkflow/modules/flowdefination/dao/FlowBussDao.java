package com.gbzt.gbztworkflow.modules.flowdefination.dao;

import com.gbzt.gbztworkflow.modules.flowdefination.entity.FlowBuss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FlowBussDao extends JpaRepository<FlowBuss,String>,JpaSpecificationExecutor<FlowBuss> {
    public List<FlowBuss> findAllByFlowId(String flowId);
}

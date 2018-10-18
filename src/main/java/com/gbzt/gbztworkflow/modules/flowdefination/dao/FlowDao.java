package com.gbzt.gbztworkflow.modules.flowdefination.dao;

import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FlowDao extends JpaRepository<Flow,String>,JpaSpecificationExecutor<Flow> {

    public Integer countFlowByFlowName(String name);

    public Flow findFlowByFlowName(String name);

}

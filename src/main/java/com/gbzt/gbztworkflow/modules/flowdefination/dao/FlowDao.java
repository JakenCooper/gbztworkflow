package com.gbzt.gbztworkflow.modules.flowdefination.dao;

import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FlowDao extends JpaRepository<Flow,String>,JpaSpecificationExecutor<Flow> {

    public Integer countFlowByFlowName(String name);

    public Flow findFlowByFlowName(String name);


    @Modifying
    @Query("update com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow flow set  flow.flowName=:flowName where flow.id=:flowId")
    public int updateFlow(@Param(value="flowName") String flowName,@Param(value="flowId") String flowId);
}

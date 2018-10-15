package com.gbzt.gbztworkflow.modules.flowdefination.dao;

import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface NodeDao extends JpaRepository<Node,String>,JpaSpecificationExecutor<Node>{

    public List<Node> findNodeByFlowIdOrderByCreateTimeDesc(String flowId);

    public Integer countNodeByNameAndFlowId(String name,String flowId);

    public List<Node> findNodesByIdIn(List<String> ids);

}

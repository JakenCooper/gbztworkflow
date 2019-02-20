package com.gbzt.gbztworkflow.modules.flowdefination.dao;

import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface NodeDao extends JpaRepository<Node,String>,JpaSpecificationExecutor<Node>{

    public List<Node> findNodeByFlowIdOrderByCreateTimeDesc(String flowId);

    public List<Node> findNodeByFlowIdOrderByNodeDefIdDesc(String flowId);

    public List<Node> findNodeByFlowIdOrderBySortNumDesc(String flowId);

    public Integer countNodeByFlowId(String flowId);

    public Integer countNodeByNameAndFlowId(String name,String flowId);

    public List<Node> findNodesByIdIn(List<String> ids);

    public Node findNodeByFlowIdAndBeginNode(String flowId,boolean flag);

    public Node findNodeByFlowIdAndEndNode(String flowId,boolean flag);

    public Node findNodeByNodeDefIdAndFlowId(String node,String flowId);

    public List<Node> findNodeByFlowIdAndDelTagAndTransferOut(String flowId,boolean delTag,boolean transferOut);

}

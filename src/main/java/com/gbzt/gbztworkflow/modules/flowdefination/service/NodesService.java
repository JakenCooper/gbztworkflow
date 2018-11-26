package com.gbzt.gbztworkflow.modules.flowdefination.service;

import com.gbzt.gbztworkflow.modules.flowdefination.dao.NodeDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NodesService {
    @Autowired
    private NodeDao nodeDao;

    public List<Node> findNodeByFlowIdOrderByCreateTimeDesc(String flowId){
        return nodeDao.findNodeByFlowIdOrderByCreateTimeDesc(flowId);
    }
}

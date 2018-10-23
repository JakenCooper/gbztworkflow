package com.gbzt.gbztworkflow.modules.flowdefination.controller;

import com.gbzt.gbztworkflow.modules.base.BaseController;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.service.NodeUserPrivService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/nodeuserpriv")
/**节点用户权限*/
public class NodeUserPrivController extends BaseController {

    @Autowired
    private NodeUserPrivService nodeUserPrivService;

    @RequestMapping(value="/{flowId}/{nodeId}",method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public ResponseEntity getTreeData(@PathVariable("flowId") String flowId,@PathVariable("nodeId") String nodeId){
        //require : nodeid flowid
        Node node = new Node();
        node.setFlowId(flowId);
        node.setId(nodeId);
        return buildResp(200,nodeUserPrivService.getAllUserInfo(node).result);
    }

}

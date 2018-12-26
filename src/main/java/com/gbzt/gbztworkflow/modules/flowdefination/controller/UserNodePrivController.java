package com.gbzt.gbztworkflow.modules.flowdefination.controller;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.base.BaseController;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.UserNodePriv;
import com.gbzt.gbztworkflow.modules.flowdefination.service.UserNodePrivCacheService;
import com.gbzt.gbztworkflow.modules.flowdefination.service.UserNodePrivService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/usernodepriv")
/**节点用户权限*/
public class UserNodePrivController extends BaseController {

    @Autowired
    private UserNodePrivService nodeUserPrivService;
    @Autowired
    private UserNodePrivCacheService nodeUserPrivCacheService;

    @RequestMapping(value="/{flowId}/{nodeId}",method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public ResponseEntity getTreeData(@PathVariable("flowId") String flowId,@PathVariable("nodeId") String nodeId){
        //require : nodeid flowid
        Node node = new Node();
        node.setFlowId(flowId);
        node.setId(nodeId);
        ExecResult execResult = null;
        if(!AppConst.REDIS_SWITCH) {
            execResult = nodeUserPrivService.getAllUserInfo(node);
        }else{
            execResult = nodeUserPrivCacheService.getAllUserInfo(node);
        }
        return buildResp(200,execResult.result);
    }

    /**
     * add by ym  tree渲染
     */
    @RequestMapping(value="tree/{flowId}/{nodeId}",method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public ResponseEntity getTreeDatabynodeid(@PathVariable("flowId") String flowId,@PathVariable("nodeId") String nodeId){
        Node node = new Node();
        node.setFlowId(flowId);
        node.setId(nodeId);
        ExecResult execResult = null;
        if(!AppConst.REDIS_SWITCH) {
            execResult = nodeUserPrivService.getAllUserInfobynodeid(node.getId());
        }else{
            execResult = nodeUserPrivCacheService.getAllUserInfobynodeid(node.getId());
        }
        return buildResp(200,execResult.result);
    }
    
    
    
    @RequestMapping(value="",method=RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity saveUserNodePriv(@RequestBody UserNodePriv userNodePriv){
        ExecResult execResult = null;
        if(!AppConst.REDIS_SWITCH) {
            execResult = nodeUserPrivService.saveUserNodePriv(userNodePriv);
        }else{
            execResult = nodeUserPrivCacheService.saveUserNodePriv(userNodePriv);
        }
        if(!execResult.charge){
            return buildResp(201,execResult.message);
        }
        return buildResp(201,execResult.result);
    }
}

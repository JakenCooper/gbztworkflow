package com.gbzt.gbztworkflow.modules.flowdefination.controller;

import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.base.BaseController;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/defination/nodes")
public class NodeController extends BaseController {
    @Autowired
    private DefinationService definationService;

    @RequestMapping(value="/{nodeid}",method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    public ResponseEntity getNodeById(@PathVariable("nodeid") String nodeId){
        ExecResult execResult = definationService.getNodeById(nodeId);
        if(!execResult.charge){
            buildResp(400,execResult.message);
        }
        return buildResp(execResult.result == null ? 404:200,execResult.result == null?"":execResult.result);
    }

    @RequestMapping(value="",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity saveNode(@RequestBody  Node node){
        ExecResult execResult = definationService.saveNode(node);
        return buildResp(execResult.charge == true ? 201:400,execResult.message);
    }


    @RequestMapping(value="/{nodeid}",method=RequestMethod.DELETE,produces = "application/json;charset=UTF-8")
    public ResponseEntity delNode(Node node){
        ExecResult execResult = definationService.delNode(node);
        return buildResp(execResult.charge == true?204:400,execResult.message);
    }

    @RequestMapping(value="/{nodeid}",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity updateNode(@PathVariable("nodeid") String nodeId,@RequestBody Node node){
        ExecResult execResult = definationService.updateNode(nodeId,node);
        if(!execResult.charge){
            buildResp(400,execResult.message);
        }
        return buildResp(execResult.charge? 204:500,execResult.result == null?"":"");
    }
}

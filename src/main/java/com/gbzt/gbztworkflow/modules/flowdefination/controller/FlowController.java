package com.gbzt.gbztworkflow.modules.flowdefination.controller;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.base.BaseController;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.FlowBuss;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Node;
import com.gbzt.gbztworkflow.modules.flowdefination.model.FlowMetadata;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationCacheService;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.flowdefination.service.FlowBussCacheService;
import com.gbzt.gbztworkflow.modules.flowdefination.service.FlowBussService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/defination/flows")
public class FlowController extends BaseController {

    @Autowired
    private DefinationService definationService;
    @Autowired
    private DefinationCacheService definationCacheService;

    @Autowired
    private FlowBussService flowBussService;
    @Autowired
    private FlowBussCacheService flowBussCacheService;

    @RequestMapping(value="/{flowid}",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<Flow> getFlowById(@PathVariable("flowid") String id){
        ExecResult<Flow> execResult = null;
        if(!AppConst.REDIS_SWITCH) {
            execResult = definationService.getFlowById(id);
        }else{
            execResult = definationCacheService.getFlowById(id);
        }
        if(!execResult.charge){
            return buildResp(400,execResult.message);
        }

//        Flow flow = definationService.getFlowById(id).result;
        Flow flow = execResult.result;
        if(flow == null){
            return buildResp(404,"");
        }
        return ResponseEntity.ok(flow);
    }

    @RequestMapping(value="",method = RequestMethod.GET)
    public ResponseEntity<List<Flow>> getAllFlows(HttpServletResponse response){
        List<Flow> flows = null;
        try {
            if(!AppConst.REDIS_SWITCH) {
                flows = definationService.getAllFlows().result;
            }else{
                flows = definationCacheService.getAllFlows().result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(flows == null || flows.size() == 0){
            flows = new ArrayList<Flow>();
        }
        return ResponseEntity.ok(flows);
    }

    @RequestMapping(value="",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public ResponseEntity saveFlow(@RequestBody Flow flow){

        ExecResult execResult = null;
        if(!AppConst.REDIS_SWITCH) {
            execResult = definationService.saveFlow(flow);
        }else{
            execResult = definationCacheService.saveFlow(flow);
        }
        return buildResp(execResult.charge == true ? 201:400,execResult.message);
    }

    @RequestMapping(value="/{flowid}",method=RequestMethod.DELETE,produces = "application/json;charset=utf-8")
    public ResponseEntity delFlow(@PathVariable("flowid") String flowId){
        Flow flow = new Flow();
        flow.setId(flowId);
        ExecResult execResult = null;
        if(!AppConst.REDIS_SWITCH) {
            execResult = definationService.delFlow(flow);
        }else{
            execResult = definationCacheService.delFlow(flow);
        }
        return buildResp(execResult.charge == true?204:400,execResult.message);
    }

    @RequestMapping(value="/{flowid}/nodes",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public ResponseEntity<List<Node>> getNodesByFlow(@PathVariable("flowid") String flowId){
        ExecResult<List<Node>> execResult = null;
        if(!AppConst.REDIS_SWITCH) {
            execResult = definationService.getNodesByFlowId(flowId);
        }else{
            execResult = definationCacheService.getNodesByFlowId(flowId);
        }
        if(!execResult.charge){
            return buildResp(400,execResult.message);
        }
        return buildResp(200,
                (execResult.result == null || execResult.result.size() == 0?new ArrayList<Node>():execResult.result));
    }

    @RequestMapping(value="/metadata",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public ResponseEntity<FlowMetadata> findMetadata(@RequestBody FlowMetadata argument){
        return null;
    }

    @RequestMapping(value="/findAllByFlowId",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String findAllByFlowId(@RequestParam(value="currentflowid", required=true) String currentflowid){
        List<FlowBuss> nameList = null;
        if(!AppConst.REDIS_SWITCH) {
            nameList = flowBussService.findAllByFlowId(currentflowid);
        }else{
            nameList = flowBussCacheService.findAllByFlowId(currentflowid);
        }

        return new Gson().toJson(nameList);
    }
}

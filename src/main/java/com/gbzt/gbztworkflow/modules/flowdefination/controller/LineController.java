package com.gbzt.gbztworkflow.modules.flowdefination.controller;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.base.BaseController;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationCacheService;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/defination/lines")
public class LineController extends BaseController {

    @Autowired
    private DefinationService definationService;
    @Autowired
    private DefinationCacheService definationCacheService;

    @RequestMapping(value="",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity saveLine(@RequestBody  Line line){
        ExecResult execResult = null;
        if(!AppConst.REDIS_SWITCH) {
            execResult = definationService.saveLine(line);
        }else{
            execResult = definationCacheService.saveLine(line);
        }
        return buildResp(execResult.charge == true ? 201:400,execResult.message);
    }

    @RequestMapping(value="",method=RequestMethod.DELETE,produces = "application/json;charset=UTF-8")
    public ResponseEntity delLine(@RequestBody Line line){
        ExecResult execResult = null;
        if(!AppConst.REDIS_SWITCH) {
            execResult = definationService.delLine(line);
        }else{
            execResult = definationCacheService.delLine(line);
        }
        return buildResp(execResult.charge == true ? 204:400,execResult.message);
    }
}

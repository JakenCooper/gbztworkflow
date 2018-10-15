package com.gbzt.gbztworkflow.modules.flowdefination.controller;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.base.BaseController;
import com.gbzt.gbztworkflow.modules.base.TreeNode;
import com.gbzt.gbztworkflow.modules.flowdefination.model.FlowMetadata;
import com.gbzt.gbztworkflow.modules.flowdefination.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/metadata")
public class MetadataController extends BaseController {

    @Autowired
    private MetadataService metadataService;

    @RequestMapping(value="/defaults",method = RequestMethod.GET)
    public ResponseEntity<FlowMetadata> getDefaultMetadata(){
        FlowMetadata metadata = new FlowMetadata();
        metadata.setBussDbType(AppConst.METADATA_DEFAULT_DBTYPE);
        metadata.setBussDbHost(AppConst.METADATA_DEFAULT_HOST);
        metadata.setBussDbPort(AppConst.METADATA_DEFAULT_PORT);
        metadata.setBussDbName(AppConst.METADATA_DEFAULT_DBNAME);
        metadata.setBussDbUserName(AppConst.METADATA_DEFAULT_USERNAME);
        metadata.setBussDbUserPwd(AppConst.METADATA_DEFAULT_USERPWD);
        return buildResp(200,metadata);
    }

    @RequestMapping(value="/tables",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity getTables(@RequestBody  FlowMetadata metadata){
        List<String> tables = new ArrayList<String>();
        for(int i = 0;i<50;i++){
            tables.add("tbl_"+(i+1));
        }
        List<TreeNode> nodes = metadataService.buildTree("oasysdb_1207",tables);
        return buildResp(200,nodes);
    }
}

package com.gbzt.gbztworkflow.modules.flowdefination.controller;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.base.BaseController;
import com.gbzt.gbztworkflow.modules.base.TreeNode;
import com.gbzt.gbztworkflow.modules.flowdefination.model.FlowMetadata;
import com.gbzt.gbztworkflow.modules.flowdefination.service.MetadataService;
import org.apache.commons.lang3.StringUtils;
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
        metadata.setBussModelPath(AppConst.METADATA_DEFAULT_MODULEPATH);

        return buildResp(200,metadata);
    }

    @RequestMapping(value="/tables",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity getTables(@RequestBody  FlowMetadata metadata){
        List<String> emptyTables = new ArrayList<String>();
        String dbname = metadata.getBussDbName();
        if(StringUtils.isBlank(dbname)){
            dbname = "unknown";
        }
        ExecResult<FlowMetadata> execResult = metadataService.getTables(metadata);
        /*for(int i = 0;i<50;i++){
            tables.add("tbl_"+(i+1));
        }
        List<TreeNode> nodes = metadataService.buildTree("oasysdb_1207",tables);*/
        return buildResp(execResult.charge == true ?200:400,execResult.result == null ? metadataService.buildTree(dbname,emptyTables)
                :metadataService.buildTree(dbname,execResult.result.getDbTables()));
    }

    @RequestMapping(value="/columns",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity getColumns(@RequestBody  FlowMetadata metadata){
        List<String> emptyColumns = new ArrayList<String>();
        String bussTableName = metadata.getBussTableName();
        if(StringUtils.isBlank(bussTableName)){
            bussTableName = "unknown";
        }
        ExecResult<FlowMetadata> execResult = metadataService.getColumnsByTable(metadata);
        return buildResp(execResult.charge == true ?200:400,execResult.result == null ? metadataService.buildTree(bussTableName,emptyColumns)
                :metadataService.buildTree(bussTableName,execResult.result.getDbTableColumns()));
    }

    @RequestMapping(value="/create",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public ResponseEntity createBussTable(@RequestBody FlowMetadata metadata){
        ExecResult<String> execResult = metadataService.createBussTable(metadata);
        return buildResp(200,execResult);
    }
}

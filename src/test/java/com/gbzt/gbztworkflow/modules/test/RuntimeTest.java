package com.gbzt.gbztworkflow.modules.test;

import com.gbzt.gbztworkflow.config.RootConfig;
import com.gbzt.gbztworkflow.modules.flowruntime.model.TaskModel;
import com.gbzt.gbztworkflow.modules.flowruntime.model.UserTreeInfo;
import com.gbzt.gbztworkflow.modules.flowruntime.service.RuntimeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
public class RuntimeTest {
    @Autowired
    private RuntimeService runtimeService;

    @Test
    public void getnextstep(){
        TaskModel taskModel = new TaskModel();
        taskModel.setFlowName("发文");
        taskModel = runtimeService.getNextStep(taskModel);
        System.out.println(taskModel.getExecResult().result);
    }

    @Test
    public void startproc(){
        TaskModel nodeModel = new TaskModel();
        nodeModel.setFlowName("喵喵？");
        nodeModel = runtimeService.getNextStep(nodeModel);
        System.out.println(nodeModel.getExecResult().result);
        Map<String,String> nodeMap = ((List<Map<String,String>>)nodeModel.getExecResult().result).get(0);

        TaskModel procModel = new TaskModel();
        procModel.setFlowId(nodeMap.get("flowId"));
        procModel.setBussId("buss-1");
        procModel.setFormKey("/xxxcontroller/aaa");
        procModel.setPassStr("2");
        procModel.setPassUser("zhangyansong");
        procModel.setAssignUser("wangluohao");

        procModel = runtimeService.startProc(procModel);
        System.out.println(procModel.getExecResult().result);
    }

    @Test
    public void getundo(){
        TaskModel undoModel = new TaskModel();
        undoModel.setPassUser("wangluohao");
        undoModel = runtimeService.getUndo(undoModel);
        System.out.println(undoModel.getExecResult().result);
    }

    @Test
    public void getusernodeprivdata(){
        TaskModel privmodel = new TaskModel();
//        privmodel.setFlowId("c469b5d2-9baf-4912-b637-9c7b9fd25fa4");
//        privmodel.setNodeId("306ae55e-af4f-44d6-acbf-cd948dd59759");
        privmodel.setFlowId("b77c3fa1-a23e-46ee-9e61-0381137bd9d8");
        privmodel.setNodeId("9312d016-5cf7-410f-8aea-b29e478bad0b");
        privmodel = runtimeService.getUserNodeData(privmodel);
        System.out.println(privmodel.getExecResult().result);
    }

    @Test
    public void gethisttask(){
        TaskModel htmodel = new TaskModel();
        htmodel.setPassUser("qujie");
        htmodel = runtimeService.getHistTask(htmodel);
        System.out.println(htmodel.getExecResult().result);
    }

    @Test
    public void getProcHistoric(){
        TaskModel historicModel = new TaskModel();
        historicModel.setProcInstId("0a8ce256-c9e5-4f6a-bc6b-814132c6a857");
        historicModel = runtimeService.getProcHistoric(historicModel);
        System.out.println(historicModel.getExecResult().result);
    }
}

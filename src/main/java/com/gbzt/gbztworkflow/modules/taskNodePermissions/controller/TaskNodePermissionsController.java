package com.gbzt.gbztworkflow.modules.taskNodePermissions.controller;

import com.gbzt.gbztworkflow.consts.ExecResult;
import com.gbzt.gbztworkflow.modules.base.BaseController;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.FlowBuss;
//import com.gbzt.gbztworkflow.modules.flowdefination.service.FlowBussService;
import com.gbzt.gbztworkflow.modules.flowdefination.service.MetadataService;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.AdviseTypeModel;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.TaskFormIdName;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.TaskNodePermissions;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.service.TaskPermissionsService;
import com.gbzt.gbztworkflow.modules.velocity.service.VelocityService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/*import java.io.IOException;
import java.io.PrintWriter;*/
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/taskNodePermissions/")
public class TaskNodePermissionsController extends BaseController {
    @Autowired
    private TaskPermissionsService taskPermissionsService;
    @Autowired
    private MetadataService metadataService;
    @Autowired
    private VelocityService velocityService;

   /* @Autowired
    private FlowBussService flowBussService;*/

    @RequestMapping(value = "savePermissions")
    @ResponseBody
    public String savePermissions(String[] array){
        Map<String,Object> map=new HashMap<>();
        int flag=taskPermissionsService.savePermissions(array);
        if(flag==0){
            map.put("msg","0");
        }else{
            map.put("msg","1");
        }
        return new Gson().toJson(map);
    }
    
    @RequestMapping(value = "saveFormIdAndName")
    @ResponseBody
    public String saveFormIdAndName(String[] array){
        Map<String,Object> map=new HashMap<>();
        int flag=0;
        String formid = array[0];
        String formname = array[1];
        List<TaskFormIdName> list=taskPermissionsService.findTaskFormNamebyformid(formid);
        if(list.size()>0){
            if(formname.equals(list.get(0).getFormName())){
                //不添加
            }else{
                //先删除 在添加
                String id = list.get(0).getId();
               int deleteflag= taskPermissionsService.deleteTaskFormNameandId(id);
                flag = taskPermissionsService.saveFormIdAndName(array);
                
            }
        }else {
             flag = taskPermissionsService.saveFormIdAndName(array);
        }
        if(flag==0){
            map.put("msg","0");
        }else{
            map.put("msg","1");
        }
        return new Gson().toJson(map);
    }

    
    
    @RequestMapping(value = "findTaskNodePermissionsByTaskNodeId",produces = "text/html;charset=UTF-8" )
    @ResponseBody
    public String findTaskNodePermissionsByTaskNodeId(String taskNodeId, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        List<TaskNodePermissions> taskNodePermissionsList=taskPermissionsService.findTaskNodePermissionsByTaskNodeId(taskNodeId);
        
        
        return new Gson().toJson(taskNodePermissionsList);
    }

    @RequestMapping(value = "deleteTaskNodePermissionsById",produces = "text/html;charset=UTF-8" )
    @ResponseBody
    public String deleteTaskNodePermissionsById(String id){
        Map<String,Object> map=new HashMap<>();
        try {
            Integer flag=taskPermissionsService.deleteTaskNodePermissionsById(id);
            if(flag!=0){
                map.put("msg","1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg","0");
        }
        return new Gson().toJson(map);
    }



    
    @RequestMapping(value = "findrealnameforhtml" )
    @ResponseBody
    public String findrealnameforhtml(String id){
        Map<String,Object> map=new HashMap<>();
        try {
            List<TaskFormIdName> list=taskPermissionsService.findTaskFormNamebyformid(id);
         if(list.size()>0){
             String name =list.get(0).getFormName();
             map.put("msg",list.get(0).getFormName());
         }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg","0");
        }
        return new Gson().toJson(map);
    }


    @RequestMapping(value = "saveAdviseType",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public ResponseEntity saveAdviseType(@RequestBody AdviseTypeModel adviseTypeModel){
        String attrName = velocityService.humpNomenclature(adviseTypeModel.getColumnName());
        String adviseInsertResult = metadataService.addAdviseSummary(adviseTypeModel.getFlowId(),attrName);
        if("-2".equals(adviseInsertResult)){
            return buildResp(200,new ExecResult(false,"服务器内部异常，请检查日志",""));
        }
        return buildResp(200,new ExecResult(true,"success",adviseInsertResult));
    }
}


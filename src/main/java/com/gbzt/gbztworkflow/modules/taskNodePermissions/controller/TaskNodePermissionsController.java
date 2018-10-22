package com.gbzt.gbztworkflow.modules.taskNodePermissions.controller;

import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.TaskNodePermissions;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.service.TaskPermissionsService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/taskNodePermissions/")
public class TaskNodePermissionsController {
    @Autowired
    private TaskPermissionsService taskPermissionsService;

    @RequestMapping(value = "savePermissions")
    @ResponseBody
    public String savePermissions(String[] array){
        Map<String,Object> map=new HashMap<>();
        int flag=taskPermissionsService.savePermissions(array);
        if(flag==0){
            map.put("msg","分配权限失败");
        }else{
            map.put("msg","分配权限成功");
        }
        return new Gson().toJson(map);
    }
}


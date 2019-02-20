package com.gbzt.gbztworkflow.modules.flowdefination.controller;


import com.gbzt.gbztworkflow.modules.flowdefination.entity.ConnectConfig;
import com.gbzt.gbztworkflow.modules.flowdefination.service.ConnectConfigService;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.security.krb5.Config;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/connectConfig/")
public class ConnectConfigController {
    @Autowired
    private ConnectConfigService connectConfigService;
    @RequestMapping(value = "load",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String load(HttpServletRequest request){
        String oaName=request.getParameter("name");
        List<ConnectConfig> connectConfigList=connectConfigService.list();
        ConnectConfig connectConfig=new ConnectConfig();
        if(connectConfigList.size()>0){
             connectConfig=connectConfigList.get(0);
        }
        return new Gson().toJson(connectConfig);
    }

    @RequestMapping(value = "operation",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String operation(HttpServletRequest request,@RequestBody ConnectConfig connectConfig){
        Map<String,String> map=new HashMap<>();
        boolean flag=false;
        if(StringUtils.isNotBlank(connectConfig.getId())){
            connectConfigService.deleteConfig(connectConfig.getId());
        }
        connectConfig.setId(UUID.randomUUID().toString());
        flag=connectConfigService.save(connectConfig);
        if(flag){
            map.put("result","success");
        }else {
            map.put("result","fail");
        }
        return new Gson().toJson(map);
    }
}

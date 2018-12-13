package com.gbzt.gbztworkflow.modules.affairConfiguer.controller;


import com.gbzt.gbztworkflow.modules.affairConfiguer.entity.AffairConfiguer;
import com.gbzt.gbztworkflow.modules.affairConfiguer.service.AffairConfiguerService;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/affairConfiguer/")
public class AffairConfiguerController {
    @Autowired
    private AffairConfiguerService affairConfiguerService;
    @RequestMapping(value = "{flowId}/list",produces = "text/javascript;charset=utf-8")
    @ResponseBody
    public String list(@PathVariable String flowId, HttpServletResponse response){
        List<AffairConfiguer> list=affairConfiguerService.list(flowId);
        Gson gson=new Gson();
        String result=gson.toJson(list);
        System.out.println(result);
        return result;
    }

    @RequestMapping(value = "/update")
    @ResponseBody
    public String update( String id, HttpServletRequest request){
        String isUsed="";
        String chColName="";
        String searchType="";
        String affairConfiguersStr=request.getParameter("affairConfiguers");
        JSONObject json=JSONObject.fromObject(affairConfiguersStr);
        int size = json.size();
        List<AffairConfiguer> list= new ArrayList<AffairConfiguer>();
        boolean flag=false;
        for (int i = 0; i < size; i++) {
            JSONObject o = (JSONObject) json.get(""+i);
            AffairConfiguer affairConfiguer = (AffairConfiguer) JSONObject.toBean(o,AffairConfiguer.class);
            isUsed=affairConfiguer.getIsUsed();
            chColName=affairConfiguer.getChColName();
            searchType=affairConfiguer.getSearchType();
            id=affairConfiguer.getId();
            list.add(affairConfiguer);
            flag=affairConfiguerService.update(isUsed,chColName,searchType,id);
        }



        return new Gson().toJson(flag);
    }
}

package com.gbzt.gbztworkflow.modules.formDesign.controller;



import com.gbzt.gbztworkflow.modules.formDesign.Util.UeditorTools;
import com.gbzt.gbztworkflow.modules.formDesign.entity.FormDesign;
import com.gbzt.gbztworkflow.modules.formDesign.service.FormDesignService;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.service.TaskPermissionsService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/formDesign/")
public class FormDesignController {
    @Autowired
    FormDesignService formDesignService;
    @Autowired
    TaskPermissionsService taskPermissionsService;
    @RequestMapping(value = "formDesign")
    public String formDesign(FormDesign formDesign){
        System.out.println("测试一哈");
        return "formDesign/formDesign.jsp";
    }
    @RequestMapping(value = "editTableWidth",produces = "text/javascript;charset=utf-8")
    @ResponseBody
    public String editTableWidth(HttpServletRequest request){
        String tableContent=request.getParameter("tableContent");
        String tableWidth=request.getParameter("tableWidth");
        String color=request.getParameter("color");
        tableContent=formDesignService.editTableWidth(tableContent,tableWidth,color);
        return new Gson().toJson(tableContent);
    }
    @RequestMapping(value = "save")
    @ResponseBody
    public String save(HttpServletRequest request , HttpServletResponse response, HttpSession session) throws ParseException {
        FormDesign formDesign=new FormDesign();
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        String type_value=request.getParameter("type_value");
        String parse_form=request.getParameter("parse_form");
        String currentFlowId=request.getParameter("currentFlowId");
        String mode=request.getParameter("mode");
        String containerWidth=request.getParameter("containerWidth");
        String color=request.getParameter("color");
        formDesign.setColor(color);
        formDesign.setContainerWidth(containerWidth);
        formDesign.setId(uuid);
        formDesign.setFormHtml(parse_form);
        formDesign.setCurrentFlowId(currentFlowId);
        formDesign.setRemark(mode);
        //解析html
        Map<String,Object> map=new HashMap<>();
       // Map<String,Object> map=formDesignService.analysisHtml(formDesign.getFormHtml());
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(parse_form).getAsJsonObject();
        parse_form=jsonObject.get("template").getAsString();
        //为每一个input元素添加label
        Document doc=formDesignService.addLabel(parse_form);
        //添加c:if标签 生成jsp 文件
        String jspCode=formDesignService.createJsp(parse_form,currentFlowId,false,true,color);
        //存放 view 页面代码
        String jspCodeView=formDesignService.createJsp(parse_form,currentFlowId,true,false,color);
        if(StringUtils.isNotBlank(jspCode)){
            formDesign.setJspCode(jspCode);
        }
        if(StringUtils.isNotBlank(jspCodeView)){
            formDesign.setJspCodeView(jspCodeView);
        }
        if(doc!=null){
            parse_form=doc.body().html();
        }
        if(StringUtils.isNotBlank(parse_form)){
            formDesign.setFormHtml(parse_form);
        }
        try {
            formDesignService.  save(formDesign);
            map.put("success",1);
        }catch (Exception e){
            e.printStackTrace();
            map.put("success",0);
        }
        return new Gson().toJson(map);
    }

    @RequestMapping(value = "list")
    public String list(Model model){
        List<FormDesign> list=formDesignService.list();
        model.addAttribute("list",list);
        return "formDesign/formDesignList.jsp";
    }

    @RequestMapping(value = "get")
    public String get(HttpServletRequest request,Model model,FormDesign formDesign){
        String flowId=request.getParameter("id");
      /*  if(StringUtils.isNotBlank(formDesign.getId())){
            formDesign.setId(id_form);
        }*/
        try {
            formDesign=formDesignService.findFormDesignByCurrentFlowId(flowId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(formDesign!=null){
            model.addAttribute("html",formDesign.getFormHtml());
            model.addAttribute("view","edit");
            model.addAttribute("formName",formDesign.getFormName());
            model.addAttribute("containerWidth",formDesign.getContainerWidth());
            model.addAttribute("color",formDesign.getColor());
        }
        return "formDesign/formDesign.jsp";
    }

    @RequestMapping(value = "deleteById")
    @ResponseBody
    public String deleteById(FormDesign formDesign){
      Map<String,Object> map=new HashMap<>();
        Integer flag= null;
        try {
            flag = formDesignService.delFromDesign(formDesign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("flag",flag);
      return new Gson().toJson(map);
    }

    @RequestMapping(value = "preView")
    public String preView(HttpServletRequest request, FormDesign formDesign, Model model, HttpServletResponse response){
        String id=request.getParameter("id");
        formDesign.setId(id);
        formDesign=formDesignService.get(formDesign);
        String parse_html= UeditorTools.formatStr(formDesign.getFormHtml());
       request.getSession().setAttribute("html",parse_html);
        return "formDesign/formDesignView.jsp";
    }

    @RequestMapping(value = "preViewJsp")
    public String preViewJsp(HttpServletRequest request ,Model model){
        String taskName="开始111";
        FormDesign formDesign=new FormDesign();
        formDesign.setRemark("测试");
        model.addAttribute("taskName",taskName);
        model.addAttribute("formDesign",formDesign);
        return "test/test1.jsp";
    }
}

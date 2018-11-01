package com.gbzt.gbztworkflow.modules.velocity.controller;

import com.gbzt.gbztworkflow.modules.base.BaseController;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.formDesign.Util.HtmlConstant;
import com.gbzt.gbztworkflow.modules.velocity.service.VelocityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/velocity/")
public class VelocityController extends BaseController {

    // 模板路径及路径名
    private static final String xmlTemplatePath = "template/xml";
    private static final String xmlTemplateName = "mybatis-generator-configTemplate.vm";
    private static final String controllerTemplatePath = "template/web";
    // private static final String controllerTemplatePath = "E:\\IdeaWorkSpace\\gbztworkflow\\src\\main\\resources\\template\\web";
    // private static final String controllerTemplatePath = "E:\\IdeaWorkSpace\\gbztworkflow\\target\\gbztworkflow\\WEB-INF\\classes\\template\\web";
    private static final String controllerTemplateName = "controllerTemplate.vm";
    private static final String daoTemplatePath = "template/dao";
    private static final String daoTemplateName = "daoTemplate.vm";
    private static final String serviceTemplatePath = "template/service";
    private static final String serviceTemplateName = "serviceTemplate.vm";
    private static final String JspTemplateFromPath = "template/jsp/form";
    private static final String JspTemplateFromName = "JSPTemplateForm.vm";
    private static final String JspTemplateListPath = "template/jsp/list";
    private static final String JspTemplateListName = "JSPTemplateList.vm";
    private static final String JspTemplateViewPath = "template/jsp/view";
    private static final String JspTemplateViewName = "JSPTemplateView.vm";
    private static final String JspTemplateAuditPath = "template/jsp/audit";
    private static final String JspTemplateAuditName = "JSPTemplateAudit.vm";

    @Autowired
    private VelocityService velocityService;


    @RequestMapping(value="velocity")
    public String AutomaticallGenerat(HttpServletRequest request,HttpServletResponse response){
        String id = request.getParameter("id");

        Flow flow = velocityService.findListByFlowId(id);
        // 模块名
        String Package = flow.getModuleName();
        // 模块中文名(jsp标题显示文字)
        String fileTitle = flow.getModuleNameCn();
        // 生成文件路径
        String generateFilePath = flow.getModuleRootPath();// 绝对路径E:\IdeaWorkSpace\party-oa(copy)\src
        String javaFilePath = generateFilePath+"src/main/java/com/thinkgem/jeesite/modules/";
        String webappFilePath = generateFilePath+"src/main/webapp/WEB-INF/views/modules/";
        // 表名
        String table_name = flow.getBussTableName();
        String entityName = velocityService.humpNomenclature(table_name);//表名转驼峰
        String EntityName = velocityService.toUpperCaseFirstOne(entityName);// 首字母大写
        
        String bussDbHost = flow.getBussDbHost();
        String bussDbPort = flow.getBussDbPort();
        String bussDbName = flow.getBussDbName();
        String bussDbUserName = flow.getBussDbUserName();
        String bussDbUserPwd = flow.getBussDbUserPwd();
        
        
        // 生成的 逆向工程xml文件名
        String mapperName = entityName+"_generator_config";
        Map<String,String> datas = new HashMap<>();
        datas.put("table_name",table_name);
        datas.put("bussDbHost",bussDbHost);
        datas.put("bussDbPort",bussDbPort);
        datas.put("bussDbName",bussDbName);
        datas.put("bussDbUserName",bussDbUserName);
        datas.put("bussDbUserPwd",bussDbUserPwd);
        datas.put("EntityName",EntityName);
        datas.put("mapperName",mapperName);
        datas.put("entityName",entityName);
        datas.put("package",Package);
        datas.put("fileTitle",fileTitle);
        datas.put("filePath",javaFilePath);
        datas.put("checkState","checked=\"true\"");
        datas.put("selectState","");
        datas.put("selectTree", HtmlConstant.TREE_SELECT_TAG);
        datas.put("timeSelect",HtmlConstant.TIME_SELECT_TAG);
        // 生成文件
        datas.put("templatePath",controllerTemplatePath);
        datas.put("templateName", controllerTemplateName);
        velocityService.velocityTest(javaFilePath,datas);
        datas.put("templatePath", serviceTemplatePath);
        datas.put("templateName", serviceTemplateName);
        velocityService.velocityTest(javaFilePath,datas);

        datas.put("templatePath", xmlTemplatePath);
        datas.put("templateName", xmlTemplateName);
        datas.put("AbsolutePath",generateFilePath);


        try {
            // 生成 PoJo类及Mybatis Xml文件
            velocityService.generator(velocityService.velocityTest(javaFilePath,datas));

            // dao层 接口需要在Mybatis逆向生成之后,覆盖原来的(其自动生成的)dao接口
            datas.put("templatePath", daoTemplatePath);
            datas.put("templateName", daoTemplateName);
            velocityService.velocityTest(javaFilePath,datas);
            // 生成jsp
            datas.put("templatePath",JspTemplateFromPath);
            datas.put("templateName",JspTemplateFromName);
            velocityService.velocityTest(webappFilePath,datas);
            datas.put("templatePath",JspTemplateAuditPath);
            datas.put("templateName",JspTemplateAuditName);
            velocityService.velocityTest(webappFilePath,datas);
            datas.put("templatePath",JspTemplateViewPath);
            datas.put("templateName",JspTemplateViewName);
            velocityService.velocityTest(webappFilePath,datas);
            datas.put("templatePath",JspTemplateListPath);
            datas.put("templateName",JspTemplateListName);
            velocityService.velocityTest(webappFilePath,datas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

}

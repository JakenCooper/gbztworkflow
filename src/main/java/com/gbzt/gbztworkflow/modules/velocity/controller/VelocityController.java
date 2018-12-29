package com.gbzt.gbztworkflow.modules.velocity.controller;

import com.gbzt.gbztworkflow.modules.base.BaseController;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.formDesign.Util.HtmlConstant;
import com.gbzt.gbztworkflow.modules.formDesign.entity.FormDesign;
import com.gbzt.gbztworkflow.modules.formDesign.service.FormDesignService;
import com.gbzt.gbztworkflow.modules.velocity.service.VelocityService;
import com.gbzt.gbztworkflow.utils.EntityUtils;
import com.gbzt.gbztworkflow.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/velocity/")
public class VelocityController extends BaseController {

    // 模板路径及路径名
    private static final String xmlTemplatePath = "template/xml";
    private static final String xmlTemplateName = "mybatis-generator-configTemplate.vm";
    private static final String xmlTemplateNameIgnore = "mybatis-generator-configIgnoreTemplate.vm";
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
    @Autowired
    private FormDesignService formDesignService;


    @RequestMapping(value="velocity")
    @ResponseBody
    public String AutomaticallGenerat(HttpServletRequest request,HttpServletResponse response){
        // 合并
        String flowId = request.getParameter("id");
        Flow flow = velocityService.findListByFlowId(flowId);
        FormDesign formDesign = new FormDesign();
        formDesign.setCurrentFlowId(flowId);
        formDesign = formDesignService.findFormDesignsByCurrentFlowId(formDesign);

        if(formDesign == null){
            try {
                return URLEncoder.encode( "必须先使用页面编辑器设计页面才能生成模板！","UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        // 模块名
        String Package = flow.getModuleName();
        // 模块中文名(jsp标题显示文字)
        String fileTitle = flow.getModuleNameCn();
        // 生成文件路径
        String generateFilePath = flow.getModuleRootPath();// 绝对路径E:\IdeaWorkSpace\party-oa(copy)\src
        String javaFilePath = generateFilePath+"src/main/java/com/thinkgem/jeesite/modules/";
        String webappFilePath = generateFilePath+"src/main/webapp/WEB-INF/views/modules/";
        String mappingFilePath = generateFilePath+"src/main/resources/mappings/modules/";
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
        String ignoreMapperName = entityName+"_generator_configIgnore";
        Map<String,String> datas = new HashMap<>();




        datas.put("modeRemark",formDesign.getRemark());
        datas.put("table_name",table_name);
        datas.put("flowName",flow.getFlowName());
        datas.put("bussDbHost",bussDbHost);
        datas.put("bussDbPort",bussDbPort);
        datas.put("bussDbName",bussDbName);
        datas.put("bussDbUserName",bussDbUserName);
        datas.put("bussDbUserPwd",bussDbUserPwd);
        datas.put("EntityName",EntityName);
        datas.put("mapperName",mapperName);
        datas.put("ignoreMapperName",ignoreMapperName);
        datas.put("entityName",entityName);
        datas.put("package",Package);
        datas.put("title",fileTitle);
        datas.put("filePath",javaFilePath);
        datas.put("checkState","checked=\"true\"");
        datas.put("selectState","");
        datas.put("selectTree", HtmlConstant.TREE_SELECT_TAG);
        datas.put("timeSelect",HtmlConstant.TIME_SELECT_TAG);
        datas.put("readonly","readonly");
        datas.put("checkState","checked=\"true\"");
        datas.put("selectState","");
        datas.put("selectTree", HtmlConstant.TREE_SELECT_TAG);
        datas.put("timeSelect",HtmlConstant.TIME_SELECT_TAG);
        datas.put("readonly","readonly");
        datas.put("flowDefId",flow.getId());
        datas.put("form",formDesign.getJspCode());
        datas.put("entityName_CN",flow.getModuleNameCn());
        datas.put("formView",formDesign.getJspCodeView());
        // 设置fns标签
        datas.put("fnsloginName","${fns:getUser().loginName}");
        
        String projectWarPath = null;
        try {
            // 生成文件
            datas.put("templatePath",controllerTemplatePath);
            datas.put("templateName", controllerTemplateName);
            velocityService.velocityTest(javaFilePath,datas); // 生成controller
            
            datas.put("templatePath", serviceTemplatePath);
            datas.put("templateName", serviceTemplateName);
            velocityService.velocityTest(javaFilePath,datas); // 生成service

            datas.put("templatePath", xmlTemplatePath);
            datas.put("templateName", xmlTemplateName);
            datas.put("AbsolutePath",generateFilePath);
            velocityService.velocityTest(javaFilePath,datas); // 通过xmlTemplatePath模板生成文件
            
            datas.put("mappingFilePath",mappingFilePath);
            datas.put("mapperName",mapperName);
            velocityService.generator(datas,datas.get("generatorConfigFilePath"),true);// 生成 PoJo类及Mybatis Xml文件
            
            datas.put("templateName", xmlTemplateNameIgnore);
            datas.put("templatePath", xmlTemplatePath);
            datas.put("mapperName",ignoreMapperName);
            velocityService.velocityTest(javaFilePath,datas);
            
            velocityService.generator(datas,datas.get("ignoregeneratorConfigFilePath"),false); // 生成 PoJo类及Mybatis Xml文件(替换之前生成的实体类)
            // 删除创建的临时文件(上一步创建的mapper映射文件,仅保留实体类)
            File xmlFileDir = new File(mappingFilePath+Package+"/TemporaryFiles");
            FileUtils.deleteFile(xmlFileDir);

            //改造自动生成的Pojo类,在原方法后追加通用方法.
            String pojoPath = javaFilePath+Package+"/entity/"+EntityName+".java";
            EntityUtils.addCommonMethods4Entity(pojoPath);

            // dao层 接口需要在Mybatis逆向生成之后,覆盖原来的(其自动生成的)dao接口
            datas.put("templatePath", daoTemplatePath);
            datas.put("templateName", daoTemplateName);
            velocityService.velocityTest(javaFilePath,datas);
            // 生成jsp页面
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
            try {
                return URLEncoder.encode("服务器内部错误，请检查日志","UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
        return datas.get("AbsolutePath"); // 返回项目路径
    }
    /**
     * 下载war包
     * @param projectWarPath
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="baleWar")
    @ResponseBody
    public String getWar(String absolutePath,HttpServletRequest request,HttpServletResponse response){
        return velocityService.baleWar(absolutePath);
    }
    /**
     * 根据路径下载war包
     * @param projectWarPath
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="downloadWar")
    @ResponseBody
    public String downloadWar(String projectWarPath,HttpServletRequest request,HttpServletResponse response){
        if(projectWarPath != null || projectWarPath != ""){
            String[] filePaths = projectWarPath.split("\\\\");
            String fileName = filePaths[filePaths.length-1]; //获取文件名
            String filePath = projectWarPath.replace(fileName,"");
            File file = new File(filePath, fileName);
            if (file.exists()) {
                // 设置强制下载不打开
                response.setContentType("application/force-download");
                // 设置文件名
                try {
                    if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                        fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");//firefox浏览器
                    } else {
                        //fileName = URLEncoder.encode(fileName, "UTF-8");//IE浏览器、360、谷歌
                        fileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
                    }
                    response.addHeader("Content-Disposition",
                            "attachment;fileName=\""+fileName+"\"");
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    System.out.println("Download the file successfully!");
                } catch (Exception e) {
                    System.out.println("Download the file failed!");
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else {
                // war包不存在
                // return "Not found";
            }
        }
            return null;
    }

    /**
     * 判断war包是否存在
     * @param projectWarPath
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="warExists")
    @ResponseBody
    public Boolean warExists(String projectWarPath,HttpServletRequest request,HttpServletResponse response){
        File file = new File(projectWarPath);
        return file.exists();
    }
}

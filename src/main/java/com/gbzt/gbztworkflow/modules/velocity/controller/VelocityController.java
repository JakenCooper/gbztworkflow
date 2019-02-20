package com.gbzt.gbztworkflow.modules.velocity.controller;

import com.gbzt.gbztworkflow.modules.base.BaseController;
import com.gbzt.gbztworkflow.modules.flowElement.entity.FlowElement;
import com.gbzt.gbztworkflow.modules.flowElement.service.FlowElementService;
import com.gbzt.gbztworkflow.modules.flowWordTemplet.entity.FlowWordTemplet;
import com.gbzt.gbztworkflow.modules.flowWordTemplet.service.FlowWordTempletService;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.ConnectConfigDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.ConnectConfig;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.service.ConnectConfigService;
import com.gbzt.gbztworkflow.modules.formDesign.Util.HtmlConstant;
import com.gbzt.gbztworkflow.modules.formDesign.entity.FormDesign;
import com.gbzt.gbztworkflow.modules.formDesign.service.FormDesignService;
import com.gbzt.gbztworkflow.modules.velocity.service.VelocityService;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import com.gbzt.gbztworkflow.utils.EntityUtils;
import com.gbzt.gbztworkflow.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
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

    private static boolean firstTimeSuccess = false;

    @Autowired
    private VelocityService velocityService;
    @Autowired
    private FormDesignService formDesignService;
    @Autowired
    private FlowElementService flowElementService;
    @Autowired
    private ConnectConfigService connectConfigService;
    @Autowired
    private FlowWordTempletService flowWordTempletService;
    @Autowired
    private ConnectConfigDao connectConfigDao;


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
        
        // 获取是否是否开发环境
        String environment = velocityService.getValue("app.runtime.type"); // pro: 生产环境  dev: 开发环境
        // environment = "dev";

        // 获取数据库信息
        String driverClass = velocityService.getValue("jdbc.driver"); // 根据jdbc.driver来确定jdbcPrefix
        String mysqlDriver = velocityService.getValue("jdbc.mysql.buss.driver");
        String oscarDriver = velocityService.getValue("jdbc.oscar.buss.driver");
        String mysqlPreFix = velocityService.getValue("jdbc.mysql.buss.url.prefix");
        String oscarPreFix = velocityService.getValue("jdbc.oscar.buss.url.prefix");
        String jdbcPrefix = driverClass.equals(mysqlDriver)? mysqlPreFix:oscarPreFix;
             /*
            #jdbc.Prefix = jdbc:mysql://		ip地址之前的字符串
            jdbc.Prefix = jdbc\:oscar\://
            */
        
        // 模块名
        String Package = flow.getModuleName();
        // 模块中文名(jsp标题显示文字)
        String fileTitle = flow.getModuleNameCn();
        // 生成文件路径
        List<ConnectConfig> connectConfigList=connectConfigService.list();
        if(connectConfigList.size()==0){
            // try {
                // return URLEncoder.encode( "模块生成路径未配置生成失败","UTF-8");
            // } catch (UnsupportedEncodingException e) {
            //     e.printStackTrace();
            // }
        }
        Map<String,String> datas = new HashMap<>();
        // 生成文件路径(项目根路径)
        String generateFilePath = null;
        //String generateFilePath = flow.getModuleRootPath();// 绝对路径E:\IdeaWorkSpace\party-oa(copy)\src
        // E:\IdeaWorkSpace\new_svn_work_space\OASys/
        String javaFilePath = null;
        String webappFilePath = null;
        String mappingFilePath = null;
        String wordResourcePath = null; // oa word模板存放路径
        if("dev".equals(environment)){ // 开发环境
           // generateFilePath = connectConfigList.get(0).getModulePath();
           //generateFilePath = velocityService.getValue("jdbc.buss.default.modelpath") == null ?connectConfigList.get(0).getModulePath() : velocityService.getValue("jdbc.buss.default.modelpath");
           generateFilePath = "E:/workspace_idea/gbztworkflow_new/trunk/OASys/";
           if(null != generateFilePath){
               if(generateFilePath.charAt(generateFilePath.length()-1) != '/' && generateFilePath.charAt(generateFilePath.length()-1) != '\\'){
                   generateFilePath = generateFilePath+"/";
               }
           }else {
               try {
                   return URLEncoder.encode( "模块生成路径未配置生成失败","UTF-8");
               } catch (UnsupportedEncodingException e) {
                   e.printStackTrace();
               }
           }
            // generateFilePath = "E:\\workspace_idea\\gbztworkflow_new\\trunk\\OASys";
            javaFilePath = generateFilePath + "/src/main/java/com/thinkgem/jeesite/modules/";
            webappFilePath = generateFilePath + "/src/main/webapp/WEB-INF/views/modules/";
            mappingFilePath = generateFilePath + "/src/main/resources/mappings/modules/";
            wordResourcePath = generateFilePath+ "/src/main/resources/templates/modules/doc/Print_template/";
            datas.put("javaSuffix","src/main/java");
            datas.put("mapperSuffix","src/main/resources");
        }else { // 生产环境
            generateFilePath = velocityService.getRootPath4Production(); // 获取的是target目录下的oa 应该获取的是\src\main\resources resources目录下的oa
            // generateFilePath = generateFilePath.split("target")[0]+"src/main/resources/oa/";
            // generateFilePath = this.getClass().getClassLoader().getResource("./prop/file.txt").getPath();
            javaFilePath = generateFilePath + "src/com/thinkgem/jeesite/modules/";
            webappFilePath = generateFilePath + "WEB-INF/views/modules/";
            mappingFilePath = generateFilePath + "WEB-INF/classes/mappings/modules/";
            wordResourcePath = generateFilePath+ "WEB-INF/classes/templates/modules/doc/Print_template/";
            datas.put("javaSuffix","src/");
            datas.put("mapperSuffix","WEB-INF/classes/");
        }
        // 表名

        String table_name = flow.getBussTableName();
        String entityName = velocityService.humpNomenclature(table_name);//表名转驼峰
        String EntityName = velocityService.toUpperCaseFirstOne(entityName);// 首字母大写

        String bussDbHost = connectConfigList.get(0).getDataBaseIp();
        String bussDbPort = connectConfigList.get(0).getDataBasePort();
        String bussDbName = connectConfigList.get(0).getDataBaseName();
        String bussDbUserName = connectConfigList.get(0).getDataBaseUserName();
        String bussDbUserPwd = connectConfigList.get(0).getDataBasePassword();


        // 生成的 逆向工程xml文件名
        String mapperName = entityName+"_generator_config";
        String ignoreMapperName = entityName+"_generator_configIgnore";


        // 获取已上传的word模板文件,并复制到oa资源文件处
        FlowWordTemplet flowWordTemplet = new FlowWordTemplet();
        flowWordTemplet.setFlowId(flow.getId());
        flowWordTemplet.setUploadUserIp(CommonUtils.getIpAddress(request));
        flowWordTemplet = flowWordTempletService.findFlowWordTempletByFlowIdAndUploadUserIp(flowWordTemplet);
        if(null != flowWordTemplet){
            datas.put("alreadyUploaded","true");
            datas.put("wordTemplatName",flowWordTemplet.getWordTempletName());
            // oa资源文件路径
            File newFile = new File(wordResourcePath+flowWordTemplet.getWordTempletName());
            File oldFile = new File(flowWordTemplet.getWordTempletAddress());
            if(!newFile.exists()){ // 文件不存在则创建,存在则覆盖
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                newFile.delete();
            }
            if(oldFile.exists()){
                FileUtils.copyFile(oldFile,newFile);
                System.out.println("文件复制成功.");
            }else {
                System.out.println("未发现上传文件.");
            }
        }else {
            datas.put("alreadyUploaded","false");
            // try {
            //     return URLEncoder.encode( "必须先上传word模板才能使用打印功能,是否上传?","UTF-8");
            // } catch (UnsupportedEncodingException e) {
            //     e.printStackTrace();
            // }
        }


        datas.put("modeRemark",formDesign.getRemark());
        datas.put("FlowId",flow.getId());
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
        datas.put("advisecontentaudit",velocityService.getAdviseContentJspCodeForAudit());
        datas.put("advisecontentview",velocityService.getAdviseContentJspCodeForView());

        datas.put("entityName_CN",flow.getModuleNameCn());
        datas.put("formView",formDesign.getJspCodeView());
        datas.put("driverClass",driverClass);
        datas.put("jdbcPrefix",jdbcPrefix);
        // 设置fns标签
        datas.put("fnsloginName","${fns:getUser().loginName}");

        String projectWarPath = null;

        /*      *******************  新增变量   ****************  start   */
        // com.gbzt.gbztworkflow.modules.velocity.web
        String controllerFullPath = "com.thinkgem.jeesite.modules."+Package+".web."+EntityName+"Controller";
        // com.thinkgem.jeesite.modules.testTable8.service.TestTable8Service
        String serviceFullPath = "com.thinkgem.jeesite.modules."+Package+".service."+EntityName+"Service";
        // com.thinkgem.jeesite.modules.testTable111.dao.TestTable111Dao
        String daoFullPath = "com.thinkgem.jeesite.modules."+Package+".dao."+EntityName+"Dao";
        // mappings/modules/testTable8/TestTable8Dao.xml
        String mapperClassPath = "mappings/modules/"+Package+"/"+EntityName+"Dao.xml";
        // 模块名称 就是 EntityName 和 entityName 中文名称 entityName_CN
        /*      *******************  新增变量   ****************   end  */
        
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
            try {
                velocityService.generator(datas,datas.get("generatorConfigFilePath"),true);// 生成 PoJo类及Mybatis Xml文件
            } catch (Exception e) {
                e.printStackTrace();
            }

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
                //？？？
                List<FlowElement> flowElementList = flowElementService.findFlowElementsByFlowId(flowId);
                Map<String,String> feMap = new HashMap<String,String>();
                for(FlowElement flowElement : flowElementList){
                    feMap.put(flowElement.getHumpName() , flowElement.getElementNameCn());
                }
                EntityUtils.addCommonMethods4Entity(pojoPath,feMap);
    
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

            if("pro".equals(environment)){
                String file = velocityService.baleAnt();
                String result = velocityService.uploadToDeployer(file);
                // error not-exist 需要手动启动tomcat服务器 exist 生成成功 ( 不需要了!)
                if(!"error".equals(result)){
                    result = "success";
                }
                System.out.println("first result ============ "+result);
                if(!"error".equals(result)){
                    result = velocityService.bindMapper(serviceFullPath,daoFullPath,entityName + "Service",
                            entityName + "Dao",mapperClassPath);
                    System.out.println("second result ============ "+result);
                    if("error".equals(result)){
                        return result;
                    }
                    result = velocityService.handleuri(controllerFullPath,serviceFullPath,entityName + "Service");
                    System.out.println("third result ============ "+result);
                }
                if(!"error".equals(result)){
                    result = "success";
                }else{
                    result = "error";
                }
                if(!firstTimeSuccess && result.equals("success")){
                    firstTimeSuccess = true;
                    return "success";
                }
                if(firstTimeSuccess){
                    return "success";
                }
                System.out.println("result ============ "+result);
                return result;
            }
            
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
     * 生成war包
     * @param absolutePath 项目路径
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
     * 根据路径下载war包
     * @param projectWarPath
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="errorDownloadWar")
    @ResponseBody
    public String errorDownloadWar(HttpServletRequest request,HttpServletResponse response){
        // 获取war包路径
        File file = null;
        String fileName = "WorkflowOA.war";
        try {
            file = ResourceUtils.getFile("classpath:oa/WorkflowOA.war");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

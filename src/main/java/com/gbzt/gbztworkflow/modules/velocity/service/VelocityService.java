package com.gbzt.gbztworkflow.modules.velocity.service;


import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.base.BaseService;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.FlowBuss;
import com.gbzt.gbztworkflow.modules.flowdefination.service.FlowBussCacheService;
import com.gbzt.gbztworkflow.modules.flowdefination.service.FlowBussService;
import com.gbzt.gbztworkflow.modules.velocity.entity.Message;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VelocityService extends BaseService {

    private Logger logger = Logger.getLogger(VelocityService.class);
    @Autowired
    private FlowDao flowDao;
    @Autowired
    private FlowBussService flowBussService;
    @Autowired
    private FlowBussCacheService flowBussCacheService;

    /**
     * 通过模板生成可用代码(Dao、Controllor、Service层代码)
     * @param filePath
     * @param datas
     * @return
     */
    public Map<String,String> velocityTest(String filePath, Map<String,String> datas){
        VelocityEngine ve = new VelocityEngine();
        Properties properties = new Properties();
        // 设置模板的路径
        String templateName = datas.get("templateName");
        String templatePath = datas.get("templatePath");
        String dir = this.getClass().getResource("/").getPath();
        properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, dir+templatePath);
        properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        properties.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        // 初始化velocity 让设置的路径生效
        ve.init(properties);
        // 模板名称
        Template t = ve.getTemplate(templateName, "UTF-8");// controller模板
        // 这个类设置了 Velocity 使用的一些配置
        VelocityContext ctx = new VelocityContext();
        // ctx 可以存入任意类型的对象或者变量,让 template 来读取

        // 实体类名称
        ctx.put("EntityName", datas.get("EntityName"));
        ctx.put("entityName", datas.get("entityName"));
        // 日期
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ctx.put("date", (df.format(new Date())));
        // 包名
        ctx.put("package", datas.get("package"));
        // 表名
        ctx.put("table_name", datas.get("table_name"));
        // 生成绝对路径
        ctx.put("AbsolutePath", datas.get("AbsolutePath"));
        // 生成路径
        ctx.put("filePath", filePath);
        // 其他参数
        ctx.put("map", datas);
        // 其他参数
        ctx.put("checkState", datas.get("checkState"));
        ctx.put("selectState", datas.get("selectState"));
        ctx.put("selectTree", datas.get("selectTree"));
        ctx.put("timeSelect", datas.get("timeSelect"));
        ctx.put("notEmpty", "${not empty");
        ctx.put("empty", "${empty");
        ctx.put("flowDefId", datas.get("flowDefId"));
        ctx.put("entityName_CN", datas.get("entityName_CN"));
        ctx.put("readonly", datas.get("readonly"));
        ctx.put("transferOutFlag1","${transferOutFlag");
        ctx.put("flowName",datas.get("flowName"));
        ctx.put("retreatFlag","${retreatFlag");
        ctx.put("wordTemplatName","未上传模板");

        Map<String,String> map = new HashMap<>();
        List<FlowBuss> flowBussList = null;
        // 如果是controller模板,则需要特殊处理(获取到所有字段名称,并放入模板)(打印方法)
            if(templateName.contains("controllerTemplate.vm") && Boolean.parseBoolean(datas.get("alreadyUploaded"))){
                // 获取该流程所有字段
                if(!AppConst.REDIS_SWITCH) {
                    flowBussList = flowBussService.findAllByFlowId(datas.get("FlowId"));
                }else{
                    flowBussList = flowBussCacheService.findAllByFlowId(datas.get("FlowId"));
                }
                for(FlowBuss flowBuss:flowBussList) {
                    // 1. 将所有的字段名转为 ${fromUnit} 这种形式
                    String key = null == flowBuss.getColumnName()?null:"${"+humpNomenclature(flowBuss.getColumnName().toLowerCase())+"}";
                    // 2. 将所有的字段名转为 getFromUnit 这种形式
                    String value = null == flowBuss.getColumnName()?null:"get"+toUpperCaseFirstOne(humpNomenclature(flowBuss.getColumnName().toLowerCase()))+"()";
                    if(null != value && null != key){
                        map.put(key,value);
                    }
                }
                ctx.put("flowBussMap",map);
                ctx.put("wordTemplatName",datas.get("wordTemplatName"));
            }
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        // 文件生成路径
        // File file = new File("E:/IdeaWorkSpace/party-oa/src/main/java/com/thinkgem/jeesite/modules/submitinformation/velocity/createdFilePackage/web/"+EntityName+".java");
        String fileSuffix = "";
        String fileName = "";
        if(templateName.contains("daoTemplate.vm")){
            fileName = datas.get("EntityName")+"Dao.java";
            fileSuffix ="/dao/";
        }else if(templateName.contains("controllerTemplate.vm")){
            fileName = datas.get("EntityName")+"Controller.java";
            fileSuffix ="/web/";
        }else if(templateName.contains("serviceTemplate.vm")){
            fileName = datas.get("EntityName")+"Service.java";
            fileSuffix ="/service/";
        }else if(templateName.contains("Form.vm")){
            fileName = datas.get("entityName")+"Form.jsp";
            fileSuffix ="/";
        }else if(templateName.contains("List.vm")){
            fileName = datas.get("entityName")+"List.jsp";
            fileSuffix ="/";
        }else if(templateName.contains("View.vm")){
            fileName = datas.get("entityName")+"View.jsp";
            fileSuffix ="/";
        }else if(templateName.contains("Audit.vm")){
            fileName = datas.get("entityName")+"Audit.jsp";
            fileSuffix ="/";
        }else if(templateName.contains("IgnoreTemplate.vm")){
            fileName = datas.get("ignoreMapperName")+".xml";
            fileSuffix ="/xml/";
            // 生成xml文件之后为generator()方法传入生成路径
            datas.put("ignoregeneratorConfigFilePath",filePath+datas.get("package")+fileSuffix+fileName);// ignroGeneratorConfigFile配置文件路径
        }else {
            fileName = datas.get("mapperName")+".xml";
            fileSuffix ="/xml/";
            // 生成xml文件之后为generator()方法传入生成路径
            datas.put("generatorConfigFilePath",filePath+datas.get("package")+fileSuffix+fileName);//generatorConfigFile配置文件路径
        }
        File file = new File(filePath+datas.get("package")+fileSuffix);
        // OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(“dd.txt”),”UTF-8”);
        OutputStream os = null;
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(filePath+datas.get("package")+fileSuffix+fileName);
            os = new BufferedOutputStream(new FileOutputStream(file,false));
            byte[]  data = sw.toString().getBytes("UTF-8");
            os.write(data, 0, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (os != null) {
                try {
                    os.close();
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("关闭输出流失败！");
                }
            }
        }
//        System.out.println(sw.toString());
        return datas;
    }

    /**
     * Mybatise根据xml文件自动生成PoJo类、mapper文件
     */
    public void generator(Map<String,String> datas,String filePath,Boolean flag) throws Exception{

        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        //指定 逆向工程配置文件
        // String path = this.getClass().getClassLoader().getResource("mybatis-generator-config.xml").getPath();
        // String path = "src/main/java/com/thinkgem/jeesite/modules/submitinformation/velocity/createdFilePackage/xml/Student_generator_config.xml";
        // 判断是否已经存在xml,存在则删除
        String deleteXmlPath = datas.get("mappingFilePath");
        File file = new File(deleteXmlPath+ datas.get("package")+"/"+datas.get("EntityName")+"Dao.xml");
        if(file.exists() && flag){
            file.delete();
        }
        String deleteXmlPath2 = datas.get("filePath");
        File file2 = new File(deleteXmlPath2+datas.get("package")+"/TemporaryFiles/"+datas.get("mapperName")+".xml");
            if(file2.exists()){
                file2.delete();
            }
        
        String path = filePath;
        File configFile = new File(path);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                callback, warnings);
        myBatisGenerator.generate(null);
    }

    /**
     * 生成War包
     * @param absolutePath 项目路径
     */
    public String baleWar(String absolutePath) {
        // 获取所在盘符
        String driveLetter = String.valueOf(absolutePath.charAt(0));
        String command="cmd /c "+driveLetter+": && cd "+absolutePath+" && mvn clean package";
        String line = null;
        StringBuilder sb = new StringBuilder();
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(command);
            BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(process.getInputStream(),"GBK"));

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
        System.out.println(sb.toString());
        return getWarPath(absolutePath);
    }

    public String getRootPath4Production(){
        String rootpath = this.getClass().getClassLoader().getResource("/oa").getPath();
        if(rootpath.startsWith("/")) {
            rootpath = rootpath.substring(1, rootpath.length());
        }
        return rootpath;
    }


    public String baleAnt(){
        String rootpath = getRootPath4Production();
        String driveLetter = String.valueOf(rootpath.charAt(0));
//        String command="cmd /c "+driveLetter+": && cd "+rootpath+" && ant compile && jar cvf "+AppConst.GLOBAL_OA_PRODUCT_NAME +".war ./*";
        String command="cmd /c "+driveLetter+": && cd "+rootpath+" && ant package ";
        String line = null;
        StringBuilder sb = new StringBuilder();
        Runtime runtime = Runtime.getRuntime();
        BufferedReader bufferedReader = null;
        try {
            Process process = runtime.exec(command);
            bufferedReader = new BufferedReader (new InputStreamReader(process.getInputStream(),"GBK"));

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        } finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(sb.toString());
        if(!rootpath.endsWith("/") && !rootpath.endsWith("\\")){
            rootpath = rootpath + "/";
        }
        String absFilePath = rootpath + AppConst.GLOBAL_OA_PRODUCT_NAME + ".war";
        File warFile = new File(absFilePath);
        if(!warFile.exists()){
            logger.error("生产环境打包文件不存在！！！！！！！！！！！！！");
            return "error";
        }
        return absFilePath;
    }

    public String uploadToDeployer(String absFilePath){
        File targetFile = new File(absFilePath);
        if(!targetFile.exists()){
            return "error";
        }
        String url = AppConst.APP_DEPLOYER_URL;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            FileBody fileBody = new FileBody(targetFile);
            HttpEntity httpEntity = MultipartEntityBuilder.create()
                    .addPart("files",fileBody).build();
            httpPost.setEntity(httpEntity);
            response = client.execute(httpPost);

            HttpEntity responseEntity = response.getEntity();
            if(responseEntity != null){
                String resultjson = EntityUtils.toString(responseEntity);
                System.out.println("resultjson ======== "+resultjson);
                JSONObject jsonObject = JSONObject.fromObject(resultjson);
                Message message = (Message)jsonObject.toBean(jsonObject,Message.class);
                if(message == null){
                    logger.error("[uploadToDeployer] 返回数据为空！");
                }
                return message.getMsg();
            }else{
                logger.error("[uploadToDeployer] 返回数据为空！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(client != null){
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "error";
    }

    public String bindMapper(String serviceClassName,String daoClassName,
                             String serviceSimpleName,String daoSimpleName,
                             String mapperPath){
        String url = AppConst.OA_DYLOADER_BINDMAPPER_URL;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        String result = "error";
        try {
            client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("serviceClassName",serviceClassName));
            nameValuePairs.add(new BasicNameValuePair("daoClassName",daoClassName));
            nameValuePairs.add(new BasicNameValuePair("serviceSimpleName",serviceSimpleName));
            nameValuePairs.add(new BasicNameValuePair("daoSimpleName",daoSimpleName));
            nameValuePairs.add(new BasicNameValuePair("mapperPath",mapperPath));
            HttpEntity requestEntity = new UrlEncodedFormEntity(nameValuePairs,"UTF-8");
            httpPost.setEntity(requestEntity);

            response = client.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if(responseEntity != null){
                result = EntityUtils.toString(responseEntity,"UTF-8");
                EntityUtils.consume(responseEntity);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[bindMapper error] ===> "+e.getMessage(),e);
            return "error";
        } finally {
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(client != null){
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取app.properties属性
     * @param key
     * @return
     */
    public String getValue(String key){
        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
        Properties prop = new Properties();
        Resource configResource = resourceLoader.getResource("classpath:app.properties");
        try {
            prop.load(configResource.getInputStream());
        } catch (IOException e) {
            System.err.println("资源加载错误，找不到资源文件");
            e.printStackTrace();
        }
        return prop.getProperty(key);
    }

    public String handleuri(String controllerClassName,String serviceClassName,
                            String serviceSimpleName){
        String url = AppConst.OA_DYLOADER_HANDLEURI_URL;
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        String result = "error";
        try {
            client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("controllerClassName",controllerClassName));
            nameValuePairs.add(new BasicNameValuePair("serviceClassName",serviceClassName));
            nameValuePairs.add(new BasicNameValuePair("serviceSimpleName",serviceSimpleName));
            HttpEntity requestEntity = new UrlEncodedFormEntity(nameValuePairs,"UTF-8");
            httpPost.setEntity(requestEntity);

            response = client.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if(responseEntity != null){
                result = EntityUtils.toString(responseEntity,"UTF-8");
                EntityUtils.consume(responseEntity);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[handleuri error] ===> "+e.getMessage(),e);
            return "error";
        } finally {
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(client != null){
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 获取自动生成的war包路径
     * @param absolutePath 项目根路径
     * @return
     */
    public String getWarPath(String absolutePath){
        String pomName="pom.xml"; // 根据pom文件名获取 <artifactId></artifactId>属性
        SAXReader reader = new SAXReader();
        File pomXml=new File(absolutePath+pomName);
        Document document = null;
        try {
            document = reader.read(pomXml);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = document.getRootElement();
        List<Element> artId=root.elements("artifactId");
        String artName=artId.get(0).getText();
        //String[] projectName = absolutePath.split("\\\\");//获取项目名
        String warPath = absolutePath+"target\\"+artName+".war";//"gbztgcoa.war";
        return warPath;
    }

    /**
     * 表名字符串转驼峰命名法
     * @param str
     * @return
     */
    public static String humpNomenclature(String str){
        str = str.toLowerCase();
        final StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile("_(\\w)");
        Matcher m = p.matcher(str);
        while (m.find()){
            m.appendReplacement(sb,m.group(1).toUpperCase());
        }
        m.appendTail(sb);
        return sb.toString();
    }
    
    //首字母转大写
    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
    
    
    public Flow findListByFlowId(String id){
        return flowDao.findById(id);
    }


    public String getAdviseContentJspCodeForAudit(){
        /*return "            <c:forEach items=\"${adviseEntityMap}\" var=\"singlerow\">\n" +
                "                <c:set var=\"adviseType\" value=\"${singlerow.key}\"/>\n" +
                "                <c:set var=\"adviselist\" value=\"${singlerow.value}\"/>\n" +
                "                <script type=\"text/javascript\">\n" +
                "                    formid = '[advisetype=\"'+'<c:out value=\"${adviseType}\"/>'+'\"]';\n" +
                "                    formele = $(formid);\n" +
                "                    if(formele.length > 0){\n" +
                "                        formparentele = formele.parent();\n" +
                "                        <c:forEach items=\"${adviselist}\" var=\"adviseentity\">\n" +
                "                            var seperator = $('<hr/>');\n" +
                "                            var contentdiv = $('<div style=\"text-align:right;padding-right:15px\"><c:out value=\"${adviseentity.content}\"/></div>');\n" +
                "                            var timedive = $('<div style=\"text-align:right;color:red;padding-right:15px\"><c:out value=\"${adviseentity.adviseTime.replace(\\\".0\\\",\\\"\\\")}\"/></div>');\n" +
                "                            formparentele.append(seperator);\n" +
                "                            formparentele.append(contentdiv);\n" +
                "                            formparentele.append(timedive);\n" +
                "                        </c:forEach>\n" +
                "                    }\n" +
                "                </script>\n" +
                "            </c:forEach>";*/
        return "            <c:forEach items=\"${adviseEntityMap}\" var=\"singlerow\">\n" +
                "                <c:set var=\"adviseType\" value=\"${singlerow.key}\"/>\n" +
                "                <c:set var=\"adviselist\" value=\"${singlerow.value}\"/>\n" +
                "                <script type=\"text/javascript\">\n" +
                "                    formid = '[advisetype=\"'+'<c:out value=\"${adviseType}\"/>'+'\"]';\n" +
                "                    formele = $(formid);\n" +
                "                    formele.val('');\n" +
                "                    if(formele.length > 0){\n" +
                "                        formparentele = formele.parent();\n" +
                "                        <c:forEach items=\"${adviselist}\" var=\"adviseentity\">\n" +
                "                            var seperator = $('<div style=\"border-top:1px dashed #ddd;margin:20px 10px\"></div>');\n" +
                "                            var contentdiv = $('<div style=\"text-align:left;padding-left:10px\"><c:out value=\"${adviseentity.content}\"/></div>');\n" +
                "                            var timedive = $('<div style=\"text-align:right;color:red;padding-right:15px\"><c:out value=\"${adviseentity.adviseUser}\"/> &nbsp;&nbsp;<c:out value=\"${adviseentity.adviseTime.replace(\\\".0\\\",\\\"\\\")}\"/></div>');\n" +
                "                            formparentele.append(seperator);\n" +
                "                            formparentele.append(contentdiv);\n" +
                "                            formparentele.append(timedive);\n" +
                "                        </c:forEach>\n" +
                "                    }\n" +
                "                </script>\n" +
                "            </c:forEach>";
    }

    public String getAdviseContentJspCodeForView(){
        return  "           <c:forEach items=\"${adviseEntityMap}\" var=\"singlerow\">\n" +
                "                <c:set var=\"adviseType\" value=\"${singlerow.key}\"/>\n" +
                "                <c:set var=\"adviselist\" value=\"${singlerow.value}\"/>\n" +
                "                <script type=\"text/javascript\">\n" +
                "                    formid = '[advisetype=\"'+'<c:out value=\"${adviseType}\"/>'+'\"]';\n" +
                "                    formele = $(formid);\n" +
                "                    formele.val('');\n" +
                "                    if(formele.length > 0){\n" +
                "                        formparentele = formele.parent();\n" +
                "                        <c:forEach items=\"${adviselist}\" var=\"adviseentity\">\n" +
                "                            var seperator = $('<div style=\"border-top:1px dashed #ddd;margin:20px 10px\"></div>');\n" +
                "                            var contentdiv = $('<div style=\"text-align:left;padding-left:10px\"><c:out value=\"${adviseentity.content}\"/></div>');\n" +
                "                            var timedive = $('<div style=\"text-align:right;color:red;padding-right:15px\"><c:out value=\"${adviseentity.adviseUser}\"/> &nbsp;&nbsp;<c:out value=\"${adviseentity.adviseTime.replace(\\\".0\\\",\\\"\\\")}\"/></div>');\n" +
                "                            formparentele.append(seperator);\n" +
                "                            formparentele.append(contentdiv);\n" +
                "                            formparentele.append(timedive);\n" +
                "                        </c:forEach>\n" +
                "                    }\n" +
                "                    formele.remove();\n" +
                "                </script>\n" +
                "            </c:forEach>";
    }

    public static void main(String[] args) throws  Exception{
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        /* System.out.println(VelocityService.class.getClassLoader().getResource("/mybatis-generator.xml"));
        String path = VelocityService.class.getClassLoader().getResource("/mybatis-generator.xml").getPath();
        System.out.println(path);*/

       /* File configFile = new File("e:/mybatistest/mybatis-generator.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                callback, warnings);
        myBatisGenerator.generate(null);*/

        System.out.println(VelocityService.humpNomenclature("office_opinion"));

    }

}

package com.gbzt.gbztworkflow.modules.velocity.service;


import com.gbzt.gbztworkflow.modules.base.BaseService;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.formDesign.Util.HtmlConstant;
import com.sun.tools.internal.ws.util.xml.XmlUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VelocityService extends BaseService {

    @Autowired
    private FlowDao flowDao;

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
        ctx.put("readonly", datas.get("readonly"));
        ctx.put("outPrintTest", "${abcd}");
        ctx.put("abcd", "123456");
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

    public static void main(String[] args) throws  Exception{
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
       /* System.out.println(VelocityService.class.getClassLoader().getResource("/mybatis-generator.xml"));
        String path = VelocityService.class.getClassLoader().getResource("/mybatis-generator.xml").getPath();
        System.out.println(path);*/
        File configFile = new File("e:/mybatistest/mybatis-generator.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                callback, warnings);
        myBatisGenerator.generate(null);
    }

}

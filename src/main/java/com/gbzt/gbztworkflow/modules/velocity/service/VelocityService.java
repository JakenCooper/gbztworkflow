package com.gbzt.gbztworkflow.modules.velocity.service;


import com.gbzt.gbztworkflow.modules.base.BaseService;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VelocityService extends BaseService {

    @Autowired
    private FlowDao flowDao;

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
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        // 文件生成路径
        // File file = new File("E:/IdeaWorkSpace/party-oa/src/main/java/com/thinkgem/jeesite/modules/submitinformation/velocity/createdFilePackage/web/"+EntityName+".java");
        String fileSuffix = "";
        String fileName = "";
        if(templateName.contains("dao")){
            fileName = datas.get("EntityName")+"Dao.java";
            fileSuffix ="/dao/";
        }else if(templateName.contains("controller")){
            fileName = datas.get("EntityName")+"Controller.java";
            fileSuffix ="/web/";
        }else if(templateName.contains("service")){
            fileName = datas.get("EntityName")+"Service.java";
            fileSuffix ="/service/";
        }else if(templateName.contains("Form")){
            fileName = datas.get("EntityName")+"Form.jsp";
            fileSuffix ="/";
        }else if(templateName.contains("List")){
            fileName = datas.get("EntityName")+"List.jsp";
            fileSuffix ="/";
        }else if(templateName.contains("View")){
            fileName = datas.get("EntityName")+"View.jsp";
            fileSuffix ="/";
        }else if(templateName.contains("Audit")){
            fileName = datas.get("EntityName")+"Audit.jsp";
            fileSuffix ="/";
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
            os = new BufferedOutputStream(new FileOutputStream(file));
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
        System.out.println(sw.toString());
        return datas;
    }

    /**
     * Mybatise根据xml文件自动生成PoJo类、mapper文件
     */
    public void generator(Map<String,String> datas) throws Exception{

        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        //指定 逆向工程配置文件
        // String path = this.getClass().getClassLoader().getResource("mybatis-generator-config.xml").getPath();
        // String path = "src/main/java/com/thinkgem/jeesite/modules/submitinformation/velocity/createdFilePackage/xml/Student_generator_config.xml";
        String path = datas.get("generatorConfigFilePath");
        File configFile = new File(path);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                callback, warnings);
        myBatisGenerator.generate(null);

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

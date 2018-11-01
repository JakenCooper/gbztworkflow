package com.gbzt.gbztworkflow.modules.velocity.test;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.*;

/**
 * 测试Velocity
 */
public class JSPVelocity {
    public static void main(String[] args) {
        String filePath = "src/main/java/com/thinkgem/jeesite/modules/";
        String JspTemplatePath = "src/main/java/com/thinkgem/jeesite/modules/submitinformation/velocity/createdFilePackage/jsp/form";
        String JspTemplateName = "JSPTemplateForm.vm";
        String EntityName = "Student";
        String mapperName = EntityName+"_generator_config";
        String entityName = "student";
        String entityname = "student";
        String author = "WangXiaoHao";
        String Package = "studenttest";
        String extendsName = "extends BaseController";
        String fileTitle = "这是一个title!";
        Map<String,String> datas = new HashMap<>();
        datas.put("EntityName",EntityName);
        datas.put("mapperName",mapperName);
        datas.put("entityName",entityName);
        datas.put("entityname",entityname);
        datas.put("author",author);
        datas.put("package",Package);
        datas.put("extendsName",extendsName);
        datas.put("interfaceName","");
        datas.put("filePath",filePath);
        datas.put("title",fileTitle);
        datas.put("filePath",filePath);


        VelocityEngine ve = new VelocityEngine();
        Properties properties = new Properties();
        // 设置模板的路径
        properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, JspTemplatePath);
        // 初始化velocity 让设置的路径生效
        ve.init(properties);
        /* next, get the Template */
        Template t = ve.getTemplate(JspTemplateName);

        //这个类设置了 Velocity 使用的一些配置
        VelocityContext ctx = new VelocityContext();
        ctx.put("map",datas);
        StringWriter sw = new StringWriter();

        t.merge(ctx, sw);
        System.out.println(sw.toString());
    }
}







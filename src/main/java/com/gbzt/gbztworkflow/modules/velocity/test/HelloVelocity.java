package com.gbzt.gbztworkflow.modules.velocity.test;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 测试Velocity
 */
public class HelloVelocity {
    public static void main(String[] args) {
        VelocityEngine ve = new VelocityEngine();
        Properties properties = new Properties();
        String basePath = "src/main/java/com/thinkgem/jeesite/modules/submitinformation/velocity/test";//这里需要这样写路径！！！
        // 设置模板的路径
        properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, basePath);
        // 初始化velocity 让设置的路径生效
        ve.init(properties);
        /* next, get the Template */
        Template t = ve.getTemplate("Hellovelocity.vm");
        
        //这个类设置了 Velocity 使用的一些配置
        VelocityContext ctx = new VelocityContext();

        ctx.put("name", "velocity");// ctx 可以存入任意类型的对象或者变量,让 template 来读取
        ctx.put("date", (new Date()).toString());

        // list测试
        List temp = new ArrayList();
        temp.add("1");
        temp.add("2");
        ctx.put("list", temp);

        StringWriter sw = new StringWriter();

        t.merge(ctx, sw);
        System.out.println(sw.toString());
    }
}







package com.gbzt.gbztworkflow.consts;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class AppConst {

    public static final Boolean STDOUT_SWITCH;

    public static final String METADATA_DEFAULT_HOST;
    public static final String METADATA_DEFAULT_PORT;
    public static final String METADATA_DEFAULT_DBNAME;
    public static final String METADATA_DEFAULT_USERNAME;
    public static final String METADATA_DEFAULT_USERPWD;
    public static final String METADATA_DEFAULT_DBTYPE;

    static{
        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
        Resource configResource = resourceLoader.getResource("classpath:app.properties");
        Properties prop = new Properties();
        try {
            prop.load(configResource.getInputStream());
        } catch (IOException e) {
            System.err.println("资源加载错误，找不到资源文件");
            e.printStackTrace();
        }
        STDOUT_SWITCH = StringUtils.isBlank(prop.getProperty("app.stdout_switch")) ||
                prop.getProperty("app.stdout_switch").equals("false") ? false:true;
        METADATA_DEFAULT_HOST = prop.getProperty("jdbc.buss.default.host");
        METADATA_DEFAULT_PORT = prop.getProperty("jdbc.buss.default.port");
        METADATA_DEFAULT_DBNAME = prop.getProperty("jdbc.buss.default.dbName");
        METADATA_DEFAULT_USERNAME = prop.getProperty("jdbc.buss.default.userName");
        METADATA_DEFAULT_USERPWD = prop.getProperty("jdbc.buss.default.userPwd");
        METADATA_DEFAULT_DBTYPE= prop.getProperty("jdbc.buss.default.dbType");
    }

}

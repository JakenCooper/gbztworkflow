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
    public static final String METADATA_DEFAULT_MODULEPATH;

    public static final Boolean REDIS_SWITCH;
    public static final String REDIS_SERVER_HOST;
    public static final String REDIS_SERVER_PORT;


    public static final Integer TASK_EXECUTION_POOL_THREAD_INIT_COUNT = 200;
    public static final String TASK_EXECUTION_TYPE_SYNC = "TASK_EXECUTION_SYNC";
    public static final String TASK_EXECUTION_TYPE_ASYNC = "TASK_EXECUTION_ASYNC";

    public static final String TASK_EXECUTION_THREAD_TYPE_SINGLE = "TASK_THREAD_SINGLE";
    public static final String TASK_EXECUTION_THREAD_TYPE_MULTI = "TASK_THREAD_MULTI";

    public static final String FLOWRUNTIME_TASKTYPE_USER = "usertask";
    public static final String FLOWRUNTIME_TASKTYPE_SYS = "systask";
    public static final String FLOWRUNTIME_SYSTASK_AUTOFINISH = "auto_finish";

    public static final String FLOWRUNTIME_FINISHTYPE_SINGLE = "single";
    public static final String FLOWRUNTIME_FINISHTYPE_MULTI = "multi";

    public static final String FLOWRUNTIME_TASK_EXEC_TYPE_BLOCK ="block";
    public static final String FLOWRUNTIME_TASK_EXEC_TYPE_CONCURRENT ="concurrent";

    public static final String GLOBAL_OA_PRODUCT_NAME = "WorkflowOA";
    public static final String APP_DEPLOYER_URL;

    public static final String APP_RUNTIME_TYPE_DEV = "dev";
    public static final String APP_RUNTIME_TYPE_PRO = "pro";
    public static final String APP_RUNTIME_TYPE;

    public static final String OA_DYLOADER_BINDMAPPER_METHOD = "/bindmapper";
    public static final String OA_DYLOADER_HANDLEURI_METHOD = "/handleuri";
    public static final String OA_DYLOADER_BINDMAPPER_URL;
    public static final String OA_DYLOADER_HANDLEURI_URL;

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
        if(StringUtils.isBlank(prop.getProperty("app.stdout_switch"))){
            STDOUT_SWITCH =  false;
        }else{
            if(prop.getProperty("app.stdout_switch").equals("true") ){
                STDOUT_SWITCH = true;
            }else{
                STDOUT_SWITCH = false;
            }
        }

        METADATA_DEFAULT_HOST = prop.getProperty("jdbc.buss.default.host");
        METADATA_DEFAULT_PORT = prop.getProperty("jdbc.buss.default.port");
        METADATA_DEFAULT_DBNAME = prop.getProperty("jdbc.buss.default.dbName");
        METADATA_DEFAULT_USERNAME = prop.getProperty("jdbc.buss.default.userName");
        METADATA_DEFAULT_USERPWD = prop.getProperty("jdbc.buss.default.userPwd");
        METADATA_DEFAULT_DBTYPE= prop.getProperty("jdbc.buss.default.dbType");
        METADATA_DEFAULT_MODULEPATH = prop.getProperty("jdbc.buss.default.modelpath");

        REDIS_SERVER_HOST = prop.getProperty("redis.server.host");
        REDIS_SERVER_PORT = prop.getProperty("redis.server.port");
        String redisSwitch = prop.getProperty("redis.server.switch");
        if("true".equals(redisSwitch)){
            REDIS_SWITCH = true;
        }else{
            REDIS_SWITCH = false;
        }
        APP_DEPLOYER_URL = prop.getProperty("appdeployer.url");
        APP_RUNTIME_TYPE = prop.getProperty("app.runtime.type");

        OA_DYLOADER_BINDMAPPER_URL = prop.getProperty("oa.dyloader.url") + OA_DYLOADER_BINDMAPPER_METHOD;
        OA_DYLOADER_HANDLEURI_URL = prop.getProperty("oa.dyloader.url") + OA_DYLOADER_HANDLEURI_METHOD;
    }



    public static final String TASK_TEMPLATE_GETNEXTSTEP_SYNC = "TASK_TEMPLATE_GETNEXTSTEP_SYNC";
    public static final String TASK_TEMPLATE_STARTPROC_SYNC = "TASK_TEMPLATE_STARTPROC_SYNC";
    public static final String TASK_TEMPLATE_CREATETASK_SYNC = "TASK_TEMPLATE_CREATETASK_SYNC";
    public static final String TASK_TEMPLATE_FINISHTASK_SYNC = "TASK_TEMPLATE_FINISHTASK_SYNC";
    public static final String TASK_TEMPLATE_GETUNDO_SYNC = "TASK_TEMPLATE_GETUNDO_SYNC";
    public static final String TASK_TEMPLATE_GETUSERNODEDTA_SYNC = "TASK_TEMPLATE_GETUSERNODEDTA_SYNC";
    public static final String TASK_TEMPLATE_GETHISTTASK_SYNC = "TASK_TEMPLATE_GETHISTTASK_SYNC";
    public static final String TASK_TEMPLATE_GETPROCHISTORIC_SYNC = "TASK_TEMPLATE_GETPROCHISTORIC_SYNC";
    public static final String TASK_TEMPLATE_RETREATTASK_SYNC = "TASK_TEMPLATE_RETREATTASK_SYNC";

    public static final String TASK_TEMPLATE_GETBUSSTABLE_SYNC = "TASK_TEMPLATE_GETBUSSTABLE_SYNC";




    /**
     *  type for special "done tasks" fetch
     * */
    //support multi department
    public static final String FLOWRUNTIME_OWNERTYPE_DEPARTMENT = "DEPARTMENT";
    public static final String FLOWRUNTIME_OWNERTYPE_COMPANY = "COMPANY";
    public static final String FLOWRUNTIME_OWNER_OPERTYPE_READONLY = "READONLY";
    public static final String FLOWRUNTIME_OWNER_OPERTYPE_OPERABLE = "OPERABLE";
    public static final String FLOWRUNTIME_OWNER_OPERTYPE_HIDDEN = "HIDDEN";

}

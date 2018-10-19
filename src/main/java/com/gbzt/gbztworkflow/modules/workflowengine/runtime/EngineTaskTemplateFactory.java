package com.gbzt.gbztworkflow.modules.workflowengine.runtime;

import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTaskTemplate;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;

import java.util.Hashtable;

public class EngineTaskTemplateFactory {
    private static boolean initialized = false;
    private static Hashtable<String, EngineTaskTemplate> templateMap = new Hashtable<String,EngineTaskTemplate>();
    private static void initialize(){
        if(initialized) {
            return;
        }

       /* FtpTaskTemplate downloadTemplate = new FtpTaskTemplate(FtpConsts.TASK_TEMPLATE_DOWNLOAD_SYNC);
        downloadTemplate.fullfillTemplateInfo("[下载文件同步任务]", FtpConsts.TASK_EXECUTION_TYPE_SYNC,FtpConsts.TASK_EXECUTION_THREAD_TYPE_SINGLE,
                FtpDownloadExecutor.class, FtpDownloadExecutor.class);
        templateMap.put(downloadTemplate.getTemplateName(), downloadTemplate);*/


    }

    public synchronized static EngineTask buildEngineTaskByTemplate(String name){
        initialize();
        if(templateMap.get(name) == null){
            throw new IllegalArgumentException("找不到对应模板，无法生成任务对象");
        }
        return templateMap.get(name);
    }

    // rule: use newinstance object as real argobj in EngineTask (busarg object just used for additional operation)
    public static EngineTask buildEngineTask(Class<? extends EngineBaseExecutor> executableCls, Class<? extends IEngineArg> argCls, Object busArg){
        try {
            EngineBaseExecutor preExecutor = executableCls.newInstance();
            return preExecutor.generateDefaultFtpTask(argCls.newInstance(), busArg);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    // rule: use argument object construncted by service object as real argobj in EngineTask (busarg object just used for additional operation)
    public static EngineTask buildEngineTask(Class<? extends EngineBaseExecutor> executableCls,IEngineArg ftpArg,Object busArg){
        try {
            EngineBaseExecutor preExecutor = executableCls.newInstance();
            return preExecutor.generateDefaultFtpTask(ftpArg, busArg);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}

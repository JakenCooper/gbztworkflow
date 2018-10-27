package com.gbzt.gbztworkflow.modules.workflowengine.runtime;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTaskTemplate;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.task.*;

import java.util.Hashtable;

public class EngineTaskTemplateFactory {
    private static boolean initialized = false;
    private static Hashtable<String, EngineTaskTemplate> templateMap = new Hashtable<String,EngineTaskTemplate>();
    private static void initialize(){
        if(initialized) {
            return;
        }

        EngineTaskTemplate nextNextTemplate = new EngineTaskTemplate(AppConst.TASK_TEMPLATE_GETNEXTSTEP_SYNC);
        nextNextTemplate.fullfillTemplateInfo("[ 查询可选节点 ]",
                AppConst.TASK_EXECUTION_TYPE_SYNC,
                AppConst.TASK_EXECUTION_THREAD_TYPE_SINGLE,
                GetNextStep.class,GetNextStep.class);
        templateMap.put(nextNextTemplate.getTemplateName(),nextNextTemplate);

        EngineTaskTemplate startProcTemplate = new EngineTaskTemplate(AppConst.TASK_TEMPLATE_STARTPROC_SYNC);
        startProcTemplate.fullfillTemplateInfo("[ 创建流程实例 ]",
                AppConst.TASK_EXECUTION_TYPE_SYNC,
                AppConst.TASK_EXECUTION_THREAD_TYPE_SINGLE,
                StartProc.class,StartProc.class);
        templateMap.put(startProcTemplate.getTemplateName(),startProcTemplate);

        EngineTaskTemplate createTaskTemplate = new EngineTaskTemplate(AppConst.TASK_TEMPLATE_CREATETASK_SYNC);
        createTaskTemplate.fullfillTemplateInfo("[ 创建流程任务 ]",
                AppConst.TASK_EXECUTION_TYPE_SYNC,
                AppConst.TASK_EXECUTION_THREAD_TYPE_SINGLE,
                CreateTask.class,CreateTask.class);
        templateMap.put(createTaskTemplate.getTemplateName(),createTaskTemplate);

        EngineTaskTemplate finishTaskTemplate = new EngineTaskTemplate(AppConst.TASK_TEMPLATE_FINISHTASK_SYNC);
        finishTaskTemplate.fullfillTemplateInfo("[ 完成流程任务 ]",
                AppConst.TASK_EXECUTION_TYPE_SYNC,
                AppConst.TASK_EXECUTION_THREAD_TYPE_SINGLE,
                FinishTask.class,FinishTask.class);
        templateMap.put(finishTaskTemplate.getTemplateName(),finishTaskTemplate);

        EngineTaskTemplate getUndoTaskTemplate = new EngineTaskTemplate(AppConst.TASK_TEMPLATE_GETUNDO_SYNC);
        getUndoTaskTemplate.fullfillTemplateInfo("[ 查询待办任务 ]",
                AppConst.TASK_EXECUTION_TYPE_SYNC,
                AppConst.TASK_EXECUTION_THREAD_TYPE_SINGLE,
                GetUndo.class,GetUndo.class);
        templateMap.put(getUndoTaskTemplate.getTemplateName(),getUndoTaskTemplate);

        EngineTaskTemplate  getUserNodeDataTaskTemplate= new EngineTaskTemplate(AppConst.TASK_TEMPLATE_GETUSERNODEDTA_SYNC);
        getUserNodeDataTaskTemplate.fullfillTemplateInfo("[ 查询节点用户权限数据 ]",
                AppConst.TASK_EXECUTION_TYPE_SYNC,
                AppConst.TASK_EXECUTION_THREAD_TYPE_SINGLE,
                GetUserNodeData.class,GetUserNodeData.class);
        templateMap.put(getUserNodeDataTaskTemplate.getTemplateName(),getUserNodeDataTaskTemplate);

        EngineTaskTemplate  getHistTaskTemplate= new EngineTaskTemplate(AppConst.TASK_TEMPLATE_GETHISTTASK_SYNC);
        getHistTaskTemplate.fullfillTemplateInfo("[ 查询用户已办数据 ]",
                AppConst.TASK_EXECUTION_TYPE_SYNC,
                AppConst.TASK_EXECUTION_THREAD_TYPE_SINGLE,
                GetHistTask.class,GetHistTask.class);
        templateMap.put(getHistTaskTemplate.getTemplateName(),getHistTaskTemplate);

        EngineTaskTemplate  getProcHistoricTemplate= new EngineTaskTemplate(AppConst.TASK_TEMPLATE_GETPROCHISTORIC_SYNC);
        getProcHistoricTemplate.fullfillTemplateInfo("[ 查询流程流转数据 ]",
                AppConst.TASK_EXECUTION_TYPE_SYNC,
                AppConst.TASK_EXECUTION_THREAD_TYPE_SINGLE,
                GetProcHistoric.class,GetProcHistoric.class);
        templateMap.put(getProcHistoricTemplate.getTemplateName(),getProcHistoricTemplate);


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
            return preExecutor.generateDefaultEngineTask(argCls.newInstance(), busArg);
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
            return preExecutor.generateDefaultEngineTask(ftpArg, busArg);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}

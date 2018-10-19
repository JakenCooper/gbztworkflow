package com.gbzt.gbztworkflow.modules.workflowengine.runtime.base;

import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.EngineTaskTemplateFactory;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.task.CreateTask;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import com.gbzt.gbztworkflow.utils.SimpleCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import javax.persistence.Column;
import java.util.Collection;

public abstract  class EngineBaseExecutor implements EngineExecutable,EngineCallback{

    private Logger logger = Logger.getLogger(EngineBaseExecutor.class);
    private static String LOGGER_TYPE_PREFIX = "EngineBaseExecutor,";

    public  abstract  <T> T executeEngineTask(EngineTask task) throws EngineRuntimeException;

    public abstract <T> T handleCallback(EngineTask task) throws EngineRuntimeException;

    public  void preHandleTask(EngineTask task) throws EngineAccessException{
        // do nothing under default situation.
        return ;
    }

    public abstract EngineTask generateDefaultEngineTask(IEngineArg iarg,Object externalArg);

    protected final EngineTask generateDefaultEngineTask(String taskType,IEngineArg argobj){
        EngineTask engineTask = new EngineTask();
        EngineTask templateTask = EngineTaskTemplateFactory.buildEngineTaskByTemplate(taskType);
        try {
            BeanUtils.copyProperties(engineTask,templateTask);
        } catch (BeansException e) {
            e.printStackTrace();
        }
        engineTask.setArgs(argobj);
        engineTask.setTaskId(CommonUtils.genUUid());
        return engineTask;
    }

    protected final Flow getFlowComplete(DefinationService definationService,String flowId){
        // TODO not necessary
        definationService.generateDetailDefination(flowId);
        //TODO cache oper..
        return  (Flow)SimpleCache.getFromCache(SimpleCache.CACHE_KEY_PREFIX_FLOW_DETAIL+flowId);
    }

    protected final boolean isBlank(String target){
        return StringUtils.isBlank(target)?true:false;
    }

    protected final boolean isBlank(Collection collection){
        return collection == null || collection.size() == 0? true:false;
    }

    protected  final boolean isNotBlank(String target){
        return !isBlank(target);
    }

    protected  final boolean isNotBlank(Collection collection){
        return !isBlank(collection);
    }
}

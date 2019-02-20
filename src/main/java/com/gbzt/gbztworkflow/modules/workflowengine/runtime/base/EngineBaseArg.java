package com.gbzt.gbztworkflow.modules.workflowengine.runtime.base;

import com.gbzt.gbztworkflow.modules.affairConfiguer.dao.AffairConfiguerDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.FlowDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.LineDao;
import com.gbzt.gbztworkflow.modules.flowdefination.dao.NodeDao;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationCacheService;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.flowdefination.service.UserNodePrivCacheService;
import com.gbzt.gbztworkflow.modules.flowdefination.service.UserNodePrivService;
import com.gbzt.gbztworkflow.modules.redis.service.JedisService;
import com.gbzt.gbztworkflow.modules.todo.service.ToDoService;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.*;
import com.gbzt.gbztworkflow.modules.workflowengine.unDoService.service.UndoService;

/**
 *  所有所需任务参数
 * */
public class EngineBaseArg  implements IEngineArg{
    /**流程定义相关-- begin */
    public FlowDao flowDao;
    public NodeDao nodeDao;
    public LineDao lineDao;
    public DefinationService definationService;
    public UserNodePrivService nodeUserPrivService;
    public DefinationCacheService definationCacheService;
    public UserNodePrivCacheService nodeUserPrivCacheService;
    /**流程定义相关-- end */

    /**流程流转相关-- begin */
    public ProcInstDao procInstDao;
    public TaskDao taskDao;
    public TaskVariableDao taskVariableDao;
    public HistTaskDao histTaskDao;
    public HistProcDao histProcDao;

    public AffairConfiguerDao ad;
    public JedisService jedisService;
    /**流程流转相关-- end */
    /*mybatis 查询待办service
    * */
    public UndoService undoService;
    //mybatis 查询已办service
    public ToDoService toDoService;
    /*
    * 提供两个重要方法：copy以及从其他arg对象拷贝基础属性的构造方法
    * */
    public void copyFrom(EngineBaseArg targetArg){
        this.flowDao = targetArg.flowDao;
        this.nodeDao = targetArg.nodeDao;
        this.lineDao = targetArg.lineDao;
        this.definationService = targetArg.definationService;
        this.nodeUserPrivService = targetArg.nodeUserPrivService;
        this.definationCacheService = targetArg.definationCacheService;
        this.nodeUserPrivCacheService = targetArg.nodeUserPrivCacheService;

        this.procInstDao = targetArg.procInstDao;
        this.taskDao = targetArg.taskDao;
        this.taskVariableDao = targetArg.taskVariableDao;
        this.histTaskDao = targetArg.histTaskDao;
        this.histProcDao = targetArg.histProcDao;
        this.jedisService = targetArg.jedisService;
        this.undoService=targetArg.undoService;
        this.toDoService=targetArg.toDoService;

        this.ad = targetArg.ad;
    }

}

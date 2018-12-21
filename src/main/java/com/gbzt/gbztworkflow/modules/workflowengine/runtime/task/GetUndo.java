package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowruntime.model.TaskModel;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskVariables;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.EngineBaseExecutor;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.base.IEngineArg;
import com.gbzt.gbztworkflow.modules.workflowengine.runtime.entity.EngineTask;
import com.gbzt.gbztworkflow.utils.CommonUtils;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

public class GetUndo extends EngineBaseExecutor {

    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_GETUNDO_SYNC;
    private static final String ERR_MULTI_VARIABLE_TYPE = "不支持多类型变量查询！";

    private Logger logger = Logger.getLogger(GetUndo.class);
    private static String LOGGER_TYPE_PREFIX = "GetUndo,";

    public static class GetUndoArg extends EngineBaseArg implements  IEngineArg{
        private String[] requiredarg = new String[]{"passUser","pageNum","pageSize","totalPage"};
        public TaskExecution execution;
        public TaskModel taskModel;

    }

    @Override
    public EngineTask generateDefaultEngineTask(IEngineArg iarg, Object externalArg) {
        GetUndo.GetUndoArg arg =(GetUndo.GetUndoArg)iarg;
        EngineTask task = super.generateDefaultEngineTask(TASK_TYPE,arg);
        return task;
    }

    @Override
    public void preHandleTask(EngineTask task) throws EngineAccessException {
        super.preHandleTask(task);
    }

    @Override
    public String executeEngineTask(EngineTask task) throws EngineRuntimeException {
        GetUndo.GetUndoArg arg =(GetUndo.GetUndoArg)task.getArgs();
        final TaskExecution execution = arg.execution;

        final List<TaskVariables> oriTaskVariables = new ArrayList<TaskVariables>();
        // !!!! 现在只支持查询单个类型的变量对象（流程类型或者任务类型），否则直接抛出异常
        final String[] typeArr = new String[1];
        if(execution.argMap != null && execution.argMap.keySet().size() !=0 ){
            for(String argKey : execution.argMap.keySet()) {
                String argValue = execution.argMap.get(argKey);
                String realKey = null;
                String varType = null;
                if (argKey.startsWith(TaskVariables.VARS_TYPE_PROC_PREFIX)) {
                    realKey = argKey.substring(argKey.indexOf(TaskVariables.VARS_TYPE_PROC_PREFIX) + 5, argKey.length());
                    varType = TaskVariables.VARS_TYPE_PROC;
                    // 类型校验
                    if(isNotBlank(typeArr[0]) && !typeArr[0].equals(varType)){
                        throw new EngineRuntimeException(ERR_MULTI_VARIABLE_TYPE);
                    }
                    typeArr[0] = TaskVariables.VARS_TYPE_PROC;
                } else if (argKey.startsWith(TaskVariables.VARS_TYPE_TASK_PREFIX)) {
                    realKey = argKey.substring(argKey.indexOf(TaskVariables.VARS_TYPE_TASK_PREFIX) + 5, argKey.length());
                    varType = TaskVariables.VARS_TYPE_TASK;
                    // 类型校验
                    if(isNotBlank(typeArr[0]) && !typeArr[0].equals(varType)){
                        throw new EngineRuntimeException(ERR_MULTI_VARIABLE_TYPE);
                    }
                    typeArr[0] = TaskVariables.VARS_TYPE_TASK;
                } else {
                    continue;
                }
                List<TaskVariables> taskVariablesList = null;
                if(!AppConst.REDIS_SWITCH) {
                    taskVariablesList = arg.taskVariableDao.findTaskVariablesByTypeAndKeyAndValue(varType, realKey, argValue);
                }else{
                    taskVariablesList = arg.jedisService.findTaskVariablesByTypeAndKeyAndValue(varType,realKey,argValue);
                }
                oriTaskVariables.addAll(taskVariablesList);
            }
        }
        Integer pageNum = execution.pageNum == null || execution.pageNum <= 0 ? 0 : execution.pageNum - 1;
        Integer pageSize = execution.pageSize == null || execution.pageSize <= 0 ? 10 : execution.pageSize;
        if(!AppConst.REDIS_SWITCH) {
            Sort sort = new Sort(Sort.Direction.DESC, "createTime");
            Specification<Task> specification = new Specification<Task>() {
                @Override
                public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<Predicate>();
                    Predicate notFinished = criteriaBuilder.notEqual(root.get("finishTag").as(String.class), "Y");
                    // TODO zhangys [!important] 测试是否会将送阅父任务查询出来！！！！
                    predicates.add(notFinished);
                    if (isNotBlank(execution.passUser)) {
                        Predicate belongtoPassUser = criteriaBuilder.equal(root.get("assignUser").as(String.class), execution.passUser);
                        predicates.add(belongtoPassUser);
                    }
                    if (isNotBlank(execution.procInstId)) {
                        Predicate belongtoprocinst = criteriaBuilder.equal(root.get("procInstId").as(String.class), execution.procInstId);
                        predicates.add(belongtoprocinst);
                    }
                    if (oriTaskVariables.size() > 0) {
                        // 按照变量类型过滤相关属性(流程类型必须按照流程实例id进行过滤——【【【【【因为不可能每一个task都提交任务变量！！！！！】】】】)
                        // 任务类型就按照任务id进行过滤，其含义是精确过滤某些任务
                        if (typeArr[0].equals(TaskVariables.VARS_TYPE_PROC)) {
                            Set<String> procInstIds = new HashSet<String>();
                            for (TaskVariables tmpTaskVariable : oriTaskVariables) {
                                procInstIds.add(tmpTaskVariable.getProcInstId());
                            }
                            Expression<String> inexpression = root.<String>get("procInstId");
                            predicates.add(inexpression.in(procInstIds));
                        } else if (typeArr[0].equals(TaskVariables.VARS_TYPE_TASK)) {
                            Set<String> taskIds = new HashSet<String>();
                            for (TaskVariables tmpTaskVariable : oriTaskVariables) {
                                taskIds.add(tmpTaskVariable.getTaskId());
                            }
                            Expression<String> inexpression = root.<String>get("id");
                            predicates.add(inexpression.in(taskIds));
                        }
                    }
                    Predicate[] predicateArray = new Predicate[predicates.size()];
                    return criteriaBuilder.and(predicates.toArray(predicateArray));
                }
            };
            Pageable pageable = new PageRequest(pageNum, pageSize, sort);
            Page<Task> pageResult = arg.taskDao.findAll(specification, pageable);
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

            arg.taskModel.setTotalPage(pageResult.getTotalPages());
            ;
            arg.taskModel.setPageNum(pageNum + 1);
            arg.taskModel.setPageSize(pageSize);
            arg.taskModel.setTotalCount(pageResult.getTotalElements());
            for (Task resultTask : pageResult.getContent()) {
                // TODO fetch variables for proc and task (cache)
                Map<String, Object> resultMap = new HashMap<String, Object>();
                Flow flowInst = super.getFlowComplete(arg.definationService, arg.definationCacheService, resultTask.getFlowId());
                resultMap.put("taskId", resultTask.getId());
                resultMap.put("flowId", resultTask.getFlowId());
                resultMap.put("flowName", flowInst.getFlowName());
                resultMap.put("assignUser", resultTask.getAssignUser());
                resultMap.put("procInstId", resultTask.getProcInstId());
                resultMap.put("nodeId", resultTask.getNodeId());
                resultMap.put("nodeName", flowInst.getNodeIdMap().get(resultTask.getNodeId()).getName());
                resultMap.put("nodeDefId", resultTask.getNodeDefId());
                resultMap.put("bussId", resultTask.getBussId());
                resultMap.put("bussTable", resultTask.getBussTable());
                resultMap.put("formKey", flowInst.getFormKey());
                resultMap.put("description", resultTask.getDescription());
                resultMap.put("startTime", resultTask.getCreateTime());
                resultMap.put("endTime", resultTask.getFinishTime());
                resultList.add(resultMap);
            }
            arg.taskModel.setExecResult(CommonUtils.buildResult(true, "", resultList));
            task.setExecutedResult(arg.taskModel);
            return "success";
        }else{

            Set<String> procInstIdFilterSet = new HashSet<String>();
            Set<String> taskIdFilterSet = new HashSet<String>();

            if(isNotBlank(oriTaskVariables)){
                if(TaskVariables.VARS_TYPE_PROC.equals(typeArr[0])){
                    for(TaskVariables taskVariables : oriTaskVariables){
                        procInstIdFilterSet.add(taskVariables.getProcInstId());
                    }
                }else if(TaskVariables.VARS_TYPE_TASK.equals(typeArr[0])){
                    for(TaskVariables taskVariables : oriTaskVariables){
                        taskIdFilterSet.add(taskVariables.getTaskId());
                    }
                }
            }

            List taskIds = arg.jedisService.findUndoTaskByUserId(execution.passUser,typeArr[0],
                    procInstIdFilterSet,taskIdFilterSet);
            if(isBlank(taskIds)){
                arg.taskModel.setTotalPage(0);
                arg.taskModel.setPageNum(pageNum + 1);
                arg.taskModel.setPageSize(pageSize);
                arg.taskModel.setTotalCount(0l);
                arg.taskModel.setExecResult(CommonUtils.buildResult(true, "", new ArrayList<Map<String, Object>>()));
                task.setExecutedResult(arg.taskModel);
                return "success";
            }

            List<Task> taskList = arg.jedisService.findTaskInIds(taskIds);
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            arg.taskModel.setTotalPage(taskList.size()%pageSize == 0?taskList.size()/pageSize:taskList.size()/pageSize+1);
            arg.taskModel.setPageNum(pageNum + 1);
            arg.taskModel.setPageSize(pageSize);
            arg.taskModel.setTotalCount(new Long(taskList.size()));

            Integer currentIdx = 0;
            for (Task resultTask : taskList) {
                Map<String, Object> resultMap = new HashMap<String, Object>();
                Flow flowInst = super.getFlowComplete(arg.definationService, arg.definationCacheService, resultTask.getFlowId());
                resultMap.put("taskId", resultTask.getId());
                resultMap.put("flowId", resultTask.getFlowId());
                resultMap.put("flowName", flowInst.getFlowName());
                resultMap.put("assignUser", resultTask.getAssignUser());
                resultMap.put("procInstId", resultTask.getProcInstId());
                resultMap.put("nodeId", resultTask.getNodeId());
                resultMap.put("nodeName", flowInst.getNodeIdMap().get(resultTask.getNodeId()).getName());
                resultMap.put("nodeDefId", resultTask.getNodeDefId());
                resultMap.put("bussId", resultTask.getBussId());
                resultMap.put("bussTable", resultTask.getBussTable());
                resultMap.put("formKey", flowInst.getFormKey());
                resultMap.put("description", resultTask.getDescription());
                resultMap.put("startTime", resultTask.getCreateTime());
                resultMap.put("endTime", resultTask.getFinishTime());
                resultList.add(resultMap);
                if(++currentIdx > pageSize){
                    break;
                }
            }
            return "success";
        }
    }

    @Override
    public TaskModel handleCallback(EngineTask task) throws EngineRuntimeException {
        return (TaskModel)task.getExecutedResult();
    }

}

package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowruntime.model.TaskModel;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.HistTask;
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

/**
 *  hist-task : history infos for user,hist-proc : history infos for proc (in order to retreat or withdraw)
 * */
public class GetHistTask extends EngineBaseExecutor {
    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_GETHISTTASK_SYNC;
    private static final String ERR_MULTI_VARIABLE_TYPE = "不支持多类型变量查询！";

    private Logger logger = Logger.getLogger(GetHistTask.class);
    private static String LOGGER_TYPE_PREFIX = "GetHistTask,";

    public static class GetHistTaskArg extends EngineBaseArg implements IEngineArg {
        private String[] requiredarg = new String[]{"passUser","pageNum","pageSize","totalPage"};
        public TaskExecution execution;
        public TaskModel taskModel;
    }

    @Override
    public EngineTask generateDefaultEngineTask(IEngineArg iarg, Object externalArg) {
        GetHistTask.GetHistTaskArg arg = (GetHistTask.GetHistTaskArg)iarg;
        EngineTask task = super.generateDefaultEngineTask(TASK_TYPE,arg);
        return task;
    }

    @Override
    public void preHandleTask(EngineTask task) throws EngineAccessException {
        GetHistTask.GetHistTaskArg arg = (GetHistTask.GetHistTaskArg)task.getArgs();
        if(isBlank(arg.execution.passUser)){
            throw new EngineAccessException("query user is empty..");
        }
    }

    @Override
    public String executeEngineTask(EngineTask task) throws EngineRuntimeException {
        GetHistTask.GetHistTaskArg arg = (GetHistTask.GetHistTaskArg)task.getArgs();
        final TaskExecution execution = arg.execution;

        Integer pageNum = execution.pageNum == null || execution.pageNum <= 0 ?0:execution.pageNum-1;
        Integer pageSize = execution.pageSize == null || execution.pageSize <= 0?10:execution.pageSize;


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
                List<TaskVariables> taskVariablesList = arg.taskVariableDao.findTaskVariablesByTypeAndKeyAndValue(varType,realKey,argValue);
                oriTaskVariables.addAll(taskVariablesList);
            }
        }


        // TODO wrong usage... must use join search !
        List<HistTask> histTasks = arg.histTaskDao.findHistTasksByUserId(execution.passUser);
        if(isBlank(histTasks)){
            arg.taskModel.setTotalPage(0);
            arg.taskModel.setPageNum(pageNum+1);
            arg.taskModel.setPageSize(pageSize);
            arg.taskModel.setTotalCount(0l);
            arg.taskModel.setExecResult(CommonUtils.buildResult(true,"",new ArrayList<Map<String,Object>>()));
            task.setExecutedResult(arg.taskModel);
            return "success";
        }
        final List<String> taskIds = new ArrayList<String>();
        for(HistTask histTask : histTasks){
            taskIds.add(histTask.getTaskId());
        }

        Sort sort = new Sort(Sort.Direction.DESC,"createTime");
        Specification<Task> specification = new Specification<Task>() {
            @Override
            public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("id"));
                for (String taskId : taskIds) {
                    in.value(taskId);
                }
                predicates.add(in);

                if(oriTaskVariables.size() > 0){
                    // 按照变量类型过滤相关属性(流程类型必须按照流程实例id进行过滤——【【【【【因为不可能每一个task都提交任务变量！！！！！】】】】)
                    // 任务类型就按照任务id进行过滤，其含义是精确过滤某些任务
                    if(typeArr[0].equals(TaskVariables.VARS_TYPE_PROC)){
                        Set<String> procInstIds = new HashSet<String>();
                        for(TaskVariables tmpTaskVariable : oriTaskVariables){
                            procInstIds.add(tmpTaskVariable.getProcInstId());
                        }
                        Expression<String> inexpression = root.<String>get("procInstId");
                        predicates.add(inexpression.in(procInstIds));
                    }else if(typeArr[0].equals(TaskVariables.VARS_TYPE_TASK)){
                        Set<String> taskIds = new HashSet<String>();
                        for(TaskVariables tmpTaskVariable : oriTaskVariables){
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
        Pageable pageable = new PageRequest(pageNum,pageSize,sort);
        Page<Task> pageResult = arg.taskDao.findAll(specification,pageable);
        List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();

        arg.taskModel.setTotalPage(pageResult.getTotalPages());;
        arg.taskModel.setPageNum(pageNum+1);
        arg.taskModel.setPageSize(pageSize);
        arg.taskModel.setTotalCount(pageResult.getTotalElements());
        for(Task resultTask : pageResult.getContent()){
            // TODO fetch variables for proc and task (cache)
            Map<String,Object> resultMap = new HashMap<String,Object>();
            Flow flowInst = super.getFlowComplete(arg.definationService,resultTask.getFlowId());
            Task lastTask = arg.taskDao.findFirstByProcInstIdOrderByCreateTimeDesc(resultTask.getProcInstId());
            resultMap.put("taskId",resultTask.getId());
            resultMap.put("flowId",resultTask.getFlowId());
            resultMap.put("flowName",flowInst.getFlowName());
            resultMap.put("assignUser",resultTask.getAssignUser());
            resultMap.put("procInstId",resultTask.getProcInstId());
            resultMap.put("nodeId",resultTask.getNodeId());
            try {
                resultMap.put("nodeName",flowInst.getNodeIdMap().get(lastTask.getNodeId()).getName());
            } catch (Exception e) {
                resultMap.put("nodeName","空");
            }
            resultMap.put("nodeDefId",resultTask.getNodeDefId());
            resultMap.put("bussId",resultTask.getBussId());
            resultMap.put("bussTable",resultTask.getBussTable());
            resultMap.put("formKey",flowInst.getFormKey());
            resultMap.put("description",resultTask.getDescription());
            resultMap.put("startTime",resultTask.getCreateTime());
            resultMap.put("endTime",resultTask.getFinishTime());
            resultList.add(resultMap);
        }
        arg.taskModel.setExecResult(CommonUtils.buildResult(true,"",resultList));
        task.setExecutedResult(arg.taskModel);
        return "success";

    }

    @Override
    public TaskModel handleCallback(EngineTask task) throws EngineRuntimeException {
        return (TaskModel)task.getExecutedResult();
    }




}

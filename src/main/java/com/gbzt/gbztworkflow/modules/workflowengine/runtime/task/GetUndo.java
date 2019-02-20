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
import org.apache.commons.lang3.StringUtils;
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
                List<TaskVariables> taskVariablesList = new ArrayList<TaskVariables>();
                if(AppConst.REDIS_SWITCH) {
                    taskVariablesList = arg.jedisService.findTaskVariablesByTypeAndKeyAndValue(varType,realKey,argValue);
                }
                oriTaskVariables.addAll(taskVariablesList);
            }
        }
        Integer pageNum = execution.pageNum == null || execution.pageNum <= 0 ? 0 : execution.pageNum - 1;
        Integer pageSize = execution.pageSize == null || execution.pageSize <= 0 ? 10 : execution.pageSize;
        if(!AppConst.REDIS_SWITCH) {

            List<Task> taskList=new ArrayList<>();

            try {
                Map<String,Object> map=new HashMap<>();
                map.put("assignUser",arg.execution.passUser);
                map.put("typeArr",typeArr);
                map.put("limits",pageSize);
                map.put("searchFlag", typeArr[0]);
                //com.gbzt.gbztworkflow.modules.workFlowPage.entity.Page page=new com.gbzt.gbztworkflow.modules.workFlowPage.entity.Page();
                Integer page_size=arg.taskModel.getPageSize();
                //page.setMaxPageSize(page_size);//每页数据大小
                Integer cuurentPage=arg.taskModel.getPageNum();//当前页
                if(cuurentPage!=null&&page_size!=null){
                    Integer pass_page=(cuurentPage-1)*page_size;
                    map.put("pass_page",pass_page);
                }
                Integer limit=page_size;

                map.put("limit",page_size);
                if(StringUtils.isBlank(arg.taskModel.getOrderBy())){
                    arg.taskModel.setOrderBy("t.create_time desc");
                }
                map.put("orderByType",arg.taskModel.getOrderBy());
                if("yes".equals(arg.taskModel.getSearchAll())){
                    map.put("isAll","yes");
                }else{
                    map.put("isAll","no");
                }
                Integer count=arg.undoService.getUndoListsCount(map);
                taskList=arg.undoService.getUndoList(map);
                if(count!=null&&page_size!=null) {
                    if (count % page_size == 0) {
                        arg.taskModel.setTotalPage(count / page_size);
                    } else {
                        arg.taskModel.setTotalPage(count / page_size + 1);
                    }
                }
                arg.taskModel.setPageNum(pageNum + 1);
                arg.taskModel.setPageSize(pageSize);
                Long ct=new Long(count);
                arg.taskModel.setTotalCount(ct);
                Long zjlCout=new Long(count);
                arg.taskModel.setCount(zjlCout);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();


            for (Task resultTask : taskList) {
                // TODO fetch variables for proc and task (cache)
                Map<String, Object> resultMap = new HashMap<String, Object>();
                Flow flowInst = super.getFlowComplete(arg.definationService, arg.definationCacheService, resultTask.getFlowId());
                resultMap.put("taskId", resultTask.getId());
                resultMap.put("flowId", resultTask.getFlowId());
                resultMap.put("flowName", flowInst.getFlowName());
                resultMap.put("assignUser", resultTask.getAssignUser());
                resultMap.put("procInstId", resultTask.getProcInstId());
                resultMap.put("nodeId", resultTask.getNodeId());
                if(flowInst.getNodeIdMap().get(resultTask.getNodeId())!=null){
                    resultMap.put("nodeName", flowInst.getNodeIdMap().get(resultTask.getNodeId()).getName());
                }
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

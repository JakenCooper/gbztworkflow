package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowruntime.model.TaskModel;
import com.gbzt.gbztworkflow.modules.todo.entity.PageEntity;
import com.gbzt.gbztworkflow.modules.todo.service.ToDoService;
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

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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


        if(!AppConst.REDIS_SWITCH) {
            List<HistTask> histTasks = arg.histTaskDao.findHistTasksByUserId(execution.passUser);
            List<HistTask> ownerTasks = arg.histTaskDao.findHistTasksByUserIdAndOwnerUser("",execution.passUser);
            if(isNotBlank(ownerTasks)) {
                histTasks.addAll(ownerTasks);
            }
            if (isBlank(histTasks)) {
                arg.taskModel.setTotalPage(0);
                arg.taskModel.setPageNum(pageNum + 1);
                arg.taskModel.setPageSize(pageSize);
                arg.taskModel.setTotalCount(0l);
                arg.taskModel.setExecResult(CommonUtils.buildResult(true, "", new ArrayList<Map<String, Object>>()));
                task.setExecutedResult(arg.taskModel);
                return "success";
            }
            final List<String> taskIds = new ArrayList<String>();
            for (HistTask histTask : histTasks) {
                taskIds.add(histTask.getTaskId());
            }
            //条件信息
            PageEntity page = new PageEntity();
            Map<String,Object> map = new HashMap<>();
            map.put("assignUser",arg.execution.passUser);
            map.put("typeArr",typeArr);
            map.put("limits",pageSize);
            map.put("searchFlag", typeArr[0]);
            Integer page_size=arg.taskModel.getPageSize();
            page.setMaxPageSize(page_size);//每页数据大小
            Integer cuurentPage=arg.taskModel.getPageNum();//当前页
            Integer pass_page=(cuurentPage-1)*page_size;
            map.put("pass_page",pass_page);
            map.put("limit",page_size);
            //设置页面分页数据信息
            if(StringUtils.isBlank(arg.taskModel.getOrderBy())){
                arg.taskModel.setOrderBy("grt.create_time desc");
            }
            map.put("orderByType",arg.taskModel.getOrderBy());
            Integer count=arg.toDoService.getTodoListsCount(map);
            if(count%page_size==0){
                arg.taskModel.setTotalPage(count/page_size);
            }else{
                arg.taskModel.setTotalPage(count/page_size+1);
            }
            arg.taskModel.setPageNum(pageNum + 1);
            arg.taskModel.setPageSize(pageSize);
            Long ct=new Long(count);
            arg.taskModel.setTotalCount(ct);
            Long zjlCout=new Long(count);
            arg.taskModel.setCount(zjlCout);
            //当前需要显示的数据集合
            List<Task> pageResult = arg.toDoService.getToDoLists(map);
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            
            Integer currentIdx = 0;
            List<String> procInstIdList = new ArrayList<>();
            for (int i = 0; i < pageResult.size(); i++) {
            	procInstIdList.add(pageResult.get(i).getProcInstId());
			}
            //List<Task> lastTask = arg.toDoService.findFirstInProcInstIdOrderByCreateTimeDesc(procInstIdList);
            for (int i = 0; i < pageResult.size(); i++) {
            	Task resultTask = (Task) pageResult.get(i);
                Map<String, Object> resultMap = new HashMap<String, Object>();
                Flow flowInst = super.getFlowComplete(arg.definationService, arg.definationCacheService, resultTask.getFlowId());
                resultMap.put("taskId", resultTask.getId());
                resultMap.put("flowId", resultTask.getFlowId());
                resultMap.put("flowName", flowInst.getFlowName());
                resultMap.put("assignUser", resultTask.getAssignUser());
                // 针对于任务指派人的特殊标记
                if(!resultTask.getAssignUser().equals(execution.passUser)){
                    resultMap.put("ownerTag",true);
                }else{
                    resultMap.put("ownerTag",false);
                }
                resultMap.put("procInstId", resultTask.getProcInstId());
                resultMap.put("nodeId", resultTask.getNodeId());
                resultMap.put("nodeName", resultTask.getNodeName());
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
            arg.taskModel.setExecResult(CommonUtils.buildResult(true, "", resultList));
            task.setExecutedResult(arg.taskModel);
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

            List<String> taskIds = arg.jedisService.findTaskInHistTaskByUserId(execution.passUser,
                    typeArr[0],procInstIdFilterSet,taskIdFilterSet);
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
//                Task lastTask = arg.taskDao.findFirstByProcInstIdOrderByCreateTimeDesc(resultTask.getProcInstId());
                resultMap.put("taskId", resultTask.getId());
                resultMap.put("flowId", resultTask.getFlowId());
                resultMap.put("flowName", flowInst.getFlowName());
                resultMap.put("assignUser", resultTask.getAssignUser());
                resultMap.put("procInstId", resultTask.getProcInstId());
                resultMap.put("nodeId", resultTask.getNodeId());
//                try {
//                    resultMap.put("nodeName", flowInst.getNodeIdMap().get(lastTask.getNodeId()).getName());
//                } catch (Exception e) {
//                    resultMap.put("nodeName", "空");
//                }
                resultMap.put("nodeMap","空");
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
            arg.taskModel.setExecResult(CommonUtils.buildResult(true, "", resultList));
            task.setExecutedResult(arg.taskModel);

        }
        return "success";

    }

    @Override
    public TaskModel handleCallback(EngineTask task) throws EngineRuntimeException {
        return (TaskModel)task.getExecutedResult();
    }




}

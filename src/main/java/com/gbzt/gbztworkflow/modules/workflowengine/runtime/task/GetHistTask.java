package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.Flow;
import com.gbzt.gbztworkflow.modules.flowdefination.service.DefinationService;
import com.gbzt.gbztworkflow.modules.flowruntime.model.TaskModel;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.HistTaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.TaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.HistTask;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetHistTask extends EngineBaseExecutor {
    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_GETHISTTASK_SYNC;

    private Logger logger = Logger.getLogger(GetHistTask.class);
    private static String LOGGER_TYPE_PREFIX = "GetHistTask,";

    public static class GetHistTaskArg implements IEngineArg {
        private String[] requiredarg = new String[]{"passUser","pageNum","pageSize","totalPage"};
        public DefinationService definationService;
        public TaskExecution execution;
        public TaskDao taskDao;
        public TaskModel taskModel;
        public HistTaskDao histTaskDao;
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
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
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
            resultMap.put("nodeName",flowInst.getNodeIdMap().get(lastTask.getNodeId()).getName());
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

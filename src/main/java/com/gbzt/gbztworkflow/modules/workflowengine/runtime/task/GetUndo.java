package com.gbzt.gbztworkflow.modules.workflowengine.runtime.task;

import com.gbzt.gbztworkflow.consts.AppConst;
import com.gbzt.gbztworkflow.modules.flowruntime.model.TaskModel;
import com.gbzt.gbztworkflow.modules.workflowengine.dao.TaskDao;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineAccessException;
import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskExecution;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetUndo extends EngineBaseExecutor {

    private static final String TASK_TYPE = AppConst.TASK_TEMPLATE_GETUNDO_SYNC;

    private Logger logger = Logger.getLogger(GetUndo.class);
    private static String LOGGER_TYPE_PREFIX = "GetUndo,";

    public static class GetUndoArg implements  IEngineArg{
        private String[] requiredarg = new String[]{"passUser","pageNum","pageSize","totalPage"};
        public TaskExecution execution;
        public TaskDao taskDao;
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
        Integer pageNum = execution.pageNum == null?0:execution.pageNum-1;
        Integer pageSize = execution.pageSize == null?10:execution.pageNum;
        Sort sort = new Sort(Sort.Direction.DESC,"createTime");
        Specification<Task> specification = new Specification<Task>() {
            @Override
            public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if(StringUtils.isNotBlank(execution.passUser)){
                    Predicate belongtoPassUser = criteriaBuilder.equal(root.get("assignUser").as(String.class),execution.passUser);
                    Predicate notFinished = criteriaBuilder.notEqual(root.get("finishTag").as(Boolean.class),false);
                    predicates.add(belongtoPassUser);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
        Pageable pageable = new PageRequest(pageNum,pageSize,sort);
        Page<Task> pageResult = arg.taskDao.findAll(specification,pageable);
        List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
        arg.taskModel.setTotalPage(pageResult.getTotalPages());;
        arg.taskModel.setPageNum(pageNum+1);
        arg.taskModel.setPageSize(pageSize);
        for(Task resultTask : pageResult.getContent()){
            Map<String,Object> resultMap = new HashMap<String,Object>();
            resultMap.put("flowId",resultTask.getFlowId());
            resultMap.put("assignUser",resultTask.getAssignUser());
            resultMap.put("procInstId",resultTask.getProcInstId());
            resultMap.put("nodeId",resultTask.getNodeId());
            resultMap.put("nodeDefId",resultTask.getNodeDefId());
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

package com.gbzt.gbztworkflow.modules.todo.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gbzt.gbztworkflow.modules.workflowengine.exception.EngineRuntimeException;
import com.gbzt.gbztworkflow.modules.workflowengine.mybatisdao.ToDoDao;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;

@Service
public class ToDoService{

	@Autowired
	private ToDoDao toDoDao;
	/**
	 * 获取当前条件下返回的数据List
	 * @param map
	 * @return List<Task>
	 */
	@Transactional("dtm")
	public List<Task> getToDoLists(Map<String,Object> map) throws EngineRuntimeException {
		//获取开始记录数
		return toDoDao.getToDoLists(map);
	}
	/**
	 * 获取当前ProcInstId   in(List<String> procInstIdList)中的数据
	 * @param procInstIdList
	 * @return
	 */
	@Transactional("dtm")
	public List<Task> findFirstInProcInstIdOrderByCreateTimeDesc(List<String> procInstIdList) {
		return toDoDao.findFirstInProcInstIdOrderByCreateTimeDesc(procInstIdList);
	}
	/**
	 * 获取当前条件下数据总条数
	 * @param map
	 * @return 
	 */
	@Transactional("dtm")
	public Integer getTodoListsCount(Map<String, Object> map) {
		return toDoDao.getTodoListsCount(map);
	}
	
}

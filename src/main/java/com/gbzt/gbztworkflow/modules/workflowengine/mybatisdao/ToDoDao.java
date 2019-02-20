package com.gbzt.gbztworkflow.modules.workflowengine.mybatisdao;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;

@Repository
public interface ToDoDao {
	
	/**
	 * 获取当前条件下返回的数据List
	 * @param map
	 * @return List<Task>
	 */
    public List<Task> getToDoLists(Map<String, Object> map);

	/**
	 * 获取当前ProcInstId   in(List<String> procInstIdList)中的数据
	 * @param procInstIdList
	 * @return
	 */
	public List<Task> findFirstInProcInstIdOrderByCreateTimeDesc(List<String> procInstIdList);

	/**
	 * 获取当前条件下数据总条数
	 * @param map
	 * @return 
	 */
	public Integer getTodoListsCount(Map<String, Object> map);
	
}

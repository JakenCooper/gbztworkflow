package com.gbzt.gbztworkflow.modules.workflowengine.dao;

import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TaskDao extends JpaRepository<Task,String>,JpaSpecificationExecutor<Task> {

    public List<Task> findTasksByParentTaskId(String parentTaskId);

    public List<Task> findTasksByAssignUser(String assignUser);

    public List<Task> findTasksByIdIn(List<String> ids);

    public List<Task> findTasksByProcInstIdInAndChildTaskTagOrderByCreateTimeDesc(List<String> procInstIds,boolean childTaskTag);

    public List<Task> findTasksByProcInstIdAndChildTaskTagOrderByCreateTimeDesc(String procInstId,boolean childTaskTag);

    public Task findFirstByProcInstIdOrderByCreateTimeDesc(String procInstId);
}

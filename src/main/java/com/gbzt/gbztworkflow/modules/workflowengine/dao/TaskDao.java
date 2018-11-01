package com.gbzt.gbztworkflow.modules.workflowengine.dao;

import com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TaskDao extends JpaRepository<Task,String>,JpaSpecificationExecutor<Task> {

    public List<Task> findTasksByParentTaskId(String parentTaskId);

    public List<Task> findTasksByAssignUser(String assignUser);

    public List<Task> findTasksByIdIn(List<String> ids);

    public List<Task> findTasksByProcInstIdInAndChildTaskTagOrderByCreateTimeDesc(List<String> procInstIds,boolean childTaskTag);

    public List<Task> findTasksByProcInstIdAndChildTaskTagOrderByCreateTimeDesc(String procInstId,boolean childTaskTag);

    public List<Task> findTasksByProcInstIdAndChildTaskTagOrderByCreateTimeMillsDesc(String procInstId,boolean childTaskTag);

    public Task findFirstByProcInstIdOrderByCreateTimeDesc(String procInstId);

    @Modifying
    @Query("update com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task a set a.finishTag=true,a.finishUser=:operUser," +
            "a.withdrawTag=true,a.withdrawDescription='收回',a.finishTime=:finishTime where a.procInstId=:procInstId")
    public void withdrawAllUnfinishedTaskByProcInstId(@Param("procInstId") String procInstId,@Param("finishTime") Date finishTime,
                                                      @Param("operUser") String operUser);

    @Modifying
    @Query("update com.gbzt.gbztworkflow.modules.workflowengine.pojo.Task a set a.finishTag=true,a.finishUser=:operUser," +
            "a.retreatTag=true,a.retreatDescription='退回',a.finishTime=:finishTime where a.procInstId=:procInstId")
    public void retreatAllUnfinishedTaskByProcInstId(@Param("procInstId") String procInstId,@Param("finishTime") Date finishTime,
                                                      @Param("operUser") String operUser);
}

package com.gbzt.gbztworkflow.modules.workflowengine.dao;

import com.gbzt.gbztworkflow.modules.workflowengine.pojo.HistTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistTaskDao extends JpaRepository<HistTask,String>,JpaSpecificationExecutor<HistTask> {

    public void deleteHistTaskByProcInstIdAndUserId(String procInstId,String userId);

    public List<HistTask> findHistTasksByUserId(String userId);

    public List<HistTask> findHistTasksByUserIdAndProcInstId(String userId,String procInstId);

    public List<HistTask> findHistTasksByUserIdAndOwnerUser(String userId,String ownerUser);


    @Modifying
    @Query("delete from com.gbzt.gbztworkflow.modules.workflowengine.pojo.HistTask a  where a.userId='' " +
            "and a.procInstId=:procInstId")
    public void delAllOwnerHistTaskInProc(@Param("procInstId") String procInstId);
}

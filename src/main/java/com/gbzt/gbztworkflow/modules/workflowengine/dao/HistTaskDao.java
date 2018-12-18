package com.gbzt.gbztworkflow.modules.workflowengine.dao;

import com.gbzt.gbztworkflow.modules.workflowengine.pojo.HistTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HistTaskDao extends JpaRepository<HistTask,String>,JpaSpecificationExecutor<HistTask> {

    public void deleteHistTaskByProcInstIdAndUserId(String procInstId,String userId);

    public List<HistTask> findHistTasksByUserId(String userId);

    public List<HistTask> findHistTasksByUserIdAndProcInstIdIn(String userId,String procInstId);
}

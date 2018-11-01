package com.gbzt.gbztworkflow.modules.workflowengine.dao;

import com.gbzt.gbztworkflow.modules.workflowengine.pojo.HistProc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface HistProcDao extends JpaRepository<HistProc,String>,JpaSpecificationExecutor<HistProc> {

    public List<HistProc> findHistProcsByProcInstIdOrderByCreateTimeMillsDesc(String procInstId);

    public List<HistProc> findHistProcsByProcInstIdOrderByCreateTimeMillsAsc(String procInstId);

}

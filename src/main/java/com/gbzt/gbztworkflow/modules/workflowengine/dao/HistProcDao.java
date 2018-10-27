package com.gbzt.gbztworkflow.modules.workflowengine.dao;

import com.gbzt.gbztworkflow.modules.workflowengine.pojo.HistProc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HistProcDao extends JpaRepository<HistProc,String>,JpaSpecificationExecutor<HistProc> {

}

package com.gbzt.gbztworkflow.modules.workflowengine.dao;

import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskVariables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskVariableDao  extends JpaRepository<TaskVariables,String>, JpaSpecificationExecutor<TaskVariables> {
}

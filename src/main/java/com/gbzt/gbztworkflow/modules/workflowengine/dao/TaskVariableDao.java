package com.gbzt.gbztworkflow.modules.workflowengine.dao;

import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TaskVariables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TaskVariableDao  extends JpaRepository<TaskVariables,String>, JpaSpecificationExecutor<TaskVariables> {

    public List<TaskVariables> findTaskVariablesByTypeAndKeyAndValue(String type,String key,String value);


}

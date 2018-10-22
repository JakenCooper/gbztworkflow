package com.gbzt.gbztworkflow.modules.taskNodePermissions.dao;

import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.TaskNodePermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TaskPermissionsDao  extends JpaRepository<TaskNodePermissions,Integer>,JpaSpecificationExecutor<TaskNodePermissions> {
    public List<TaskNodePermissions> findByTaskNodeIdAndAndCurrentFlowId(String tId,String fId);
}

package com.gbzt.gbztworkflow.modules.taskNodePermissions.dao;

import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.TaskNodePermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskPermissionsDao  extends JpaRepository<TaskNodePermissions,Integer>,JpaSpecificationExecutor<TaskNodePermissions> {
    public List<TaskNodePermissions> findByTaskNodeIdAndAndCurrentFlowId(String tId,String fId);

    public List<TaskNodePermissions> findTaskNodePermissionsByTaskNodeId(String tId);

    @Transactional
    public Integer deleteTaskNodePermissionsById(String id);
}

package com.gbzt.gbztworkflow.modules.taskNodePermissions.dao;

import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.TaskFormIdName;
import com.gbzt.gbztworkflow.modules.taskNodePermissions.entity.TaskNodePermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskFormIdNameDao  extends JpaRepository<TaskFormIdName,Integer>, JpaSpecificationExecutor<TaskFormIdName> {
    
//    public List<TaskFormIdName> findTaskFormNamebyformid(String tId);
    public List<TaskFormIdName> findTaskFormIdNamesByFormId(String formid);

    @Transactional
    public Integer deleteTaskFormIdNameById(String id);
}

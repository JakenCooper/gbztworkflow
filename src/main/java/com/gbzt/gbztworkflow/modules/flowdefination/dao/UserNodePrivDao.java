package com.gbzt.gbztworkflow.modules.flowdefination.dao;

import com.gbzt.gbztworkflow.modules.flowdefination.entity.UserNodePriv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserNodePrivDao extends JpaRepository<UserNodePriv,String>, JpaSpecificationExecutor<UserNodePriv> {
   // public List<UserNodePriv> findUserNodePrivByNodeId(String nodeId);
    public  UserNodePriv findUseroneNodePrivByNodeId(String nodeId); 
}



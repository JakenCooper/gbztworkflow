package com.gbzt.gbztworkflow.modules.flowdefination.dao;

import com.gbzt.gbztworkflow.modules.flowdefination.entity.ConnectConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ConnectConfigDao extends JpaRepository<ConnectConfig,Integer>,JpaSpecificationExecutor<ConnectConfig> {

    @Transactional
    public boolean deleteConnectConfigById(String id);

}

package com.gbzt.gbztworkflow.modules.commonFile.dao;

import com.gbzt.gbztworkflow.modules.commonFile.entity.CommonFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonFileDao  extends JpaRepository<CommonFile,Integer>,JpaSpecificationExecutor<CommonFile> {
    public List<CommonFile> findCommonFileByProcInsId(String proInsId);
}

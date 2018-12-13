package com.gbzt.gbztworkflow.modules.affairConfiguer.dao;

import com.gbzt.gbztworkflow.modules.affairConfiguer.entity.AffairConfiguer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface AffairConfiguerDao extends JpaRepository<AffairConfiguer,Integer>,JpaSpecificationExecutor<AffairConfiguer> {
    public List<AffairConfiguer> findAffairConfiguerByFlowId(String flowId);

    @Modifying
    @Query(value="UPDATE com.gbzt.gbztworkflow.modules.affairConfiguer.entity.AffairConfiguer  ac SET ac.isUsed= ?1 ,ac.chColName=?2 ,ac.searchType=?3  WHERE ac.id= ?4")
    @Transactional
    public Integer updateById(@Param("isUsed")String isUsed,@Param("chColName")String chColName,@Param("searchType")String searchType,@Param("id")String id);

    @Transactional
    public Integer deleteByFlowId(String flowId);
}

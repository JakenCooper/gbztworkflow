package com.gbzt.gbztworkflow.modules.flowdefination.dao;

import com.gbzt.gbztworkflow.modules.flowdefination.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface LineDao  extends JpaRepository<Line,String>,JpaSpecificationExecutor<Line>{

    public List<Line> findLinesByBeginNodeId(String beginNodeId);

    public List<Line> findLinesByEndNodeId(String endNodeId);

    public List<Line> findLinesByBeginNodeIdAndEndNodeId(String beginNodeId,String endNodeId);

    public Line findLineByEndNodeIdAndBeginNodeId(String  endNodeId,String beginNodeId);


}

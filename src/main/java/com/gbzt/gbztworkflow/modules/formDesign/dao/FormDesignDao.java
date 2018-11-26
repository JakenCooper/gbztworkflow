package com.gbzt.gbztworkflow.modules.formDesign.dao;


import com.gbzt.gbztworkflow.modules.formDesign.entity.FormDesign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FormDesignDao extends JpaRepository<FormDesign,Integer>,JpaSpecificationExecutor<FormDesign> {

    /**
     * @author ch
     * @return FormDesign
     */
    public FormDesign findFormDesignById(String id);

    /**
     * @author ch
     * @return int
     */
    @Transactional
    public int deleteById(String id);


    @Modifying
    @Query(value="UPDATE com.gbzt.gbztworkflow.modules.formDesign.entity.FormDesign  fd SET fd.formHtml= ?1 ,fd.jspCode=?2 ,fd.jspCodeView=?3 ,fd.remark =?4 WHERE fd.currentFlowId= ?5")
    @Transactional
    public int updateByflow(@Param("formHtml")String formHtml, @Param("jspCode")String jspCode,@Param("jspCodeView")String  jspCodeView,@Param("remark")String remark,@Param("currentFlowId")String currentFlowId);

    /**
     * @author ch
     * @return FormDesign
     */
    public FormDesign findFormDesignByCurrentFlowId(String currentFlowId);


}

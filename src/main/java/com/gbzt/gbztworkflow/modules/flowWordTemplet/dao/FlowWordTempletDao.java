package com.gbzt.gbztworkflow.modules.flowWordTemplet.dao;


import com.gbzt.gbztworkflow.modules.flowWordTemplet.entity.FlowWordTemplet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FlowWordTempletDao extends JpaRepository<FlowWordTemplet,Integer>,JpaSpecificationExecutor<FlowWordTemplet> {

    /**
     * 根据flowId 及uploadUserIp 查找数据
     * @param flowId
     * @param uploadUserIp
     * @return
     */
    public FlowWordTemplet findFlowWordTempletByFlowIdAndUploadUserIp(String flowId,String uploadUserIp);

    /**
     * 根据id删除 FlowWordTemplet
     * @param id
     * @return
     */
    @Transactional
    public Integer deleteFlowWordTempletById(String id);
    // /**
    //  * @author ch
    //  * @return FormDesign
    //  */
    // public FlowWordTemplet findFormDesignsByCurrentFlowId(String id);
    // /**
    //  * @author ch
    //  * @return FormDesign
    //  */
    // public FlowWordTemplet findFormDesignById(String id);
    //
    // /**
    //  * @author ch
    //  * @return int
    //  */
    // @Transactional
    // public int deleteById(String id);


    // @Modifying
    // @Query(value="UPDATE com.gbzt.gbztworkflow.modules.formDesign.entity.FormDesign  fd SET fd.formHtml= ?1 ,fd.jspCode=?2 ,fd.jspCodeView=?3 ,fd.remark =?4 ,fd.formName =?5 ,fd.containerWidth =?6 ,fd.color =?7 WHERE fd.currentFlowId= ?8")
    // @Transactional
    // public int updateByflow(@Param("formHtml") String formHtml, @Param("jspCode") String jspCode, @Param("jspCodeView") String jspCodeView, @Param("remark") String remark, @Param("formName") String formName, @Param("containerWidth") String containerWidth, @Param("color") String color, @Param("currentFlowId") String currentFlowId);
    //
    // /**
    //  * @author ch
    //  * @return FormDesign
    //  */
    // public FlowWordTemplet findFormDesignByCurrentFlowId(String currentFlowId);


}

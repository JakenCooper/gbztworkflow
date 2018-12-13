package com.gbzt.gbztworkflow.modules.affairConfiguer.service;

import com.gbzt.gbztworkflow.modules.affairConfiguer.dao.AffairConfiguerDao;
import com.gbzt.gbztworkflow.modules.affairConfiguer.entity.AffairConfiguer;
import com.gbzt.gbztworkflow.modules.formDesign.Util.UeditorTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class AffairConfiguerService {
    @Autowired
    AffairConfiguerDao affairConfiguerDao;

    /*
    * 添加事务查询数据
    * flowId 流程id
    * colName 字段名称
    * */
    public Boolean save(String flowId,String[] colName){

        Boolean flag=false;
        try {
            List<AffairConfiguer> list=affairConfiguerDao.findAffairConfiguerByFlowId(flowId);
            if(list!=null){
                affairConfiguerDao.deleteByFlowId(flowId);
            }
            for(int i=0;i<colName.length;i++){

                if(colName[i].contains("_")){
                    colName[i]= UeditorTools.humpNomenclature(colName[i]);
                }

                if("title".equals(colName[i])||"articleSize".equals(colName[i])||"remarks".equals(colName[i])||"createDate".equals(colName[i])){
                    continue;
                }
                AffairConfiguer affairConfiguer=new AffairConfiguer();
                affairConfiguer.setId(UUID.randomUUID().toString());
                affairConfiguer.setFlowId(flowId);
                affairConfiguer.setCreateDate(new Date());
                affairConfiguer.setColName(colName[i]);
                affairConfiguer.setDelFlag("0");

                affairConfiguerDao.save(affairConfiguer);
                flag=true;
            }
        } catch (Exception e) {
            flag=false;
            e.printStackTrace();
        }
        return flag;
    }

    public Boolean update(String isUsed,String chColName,String searchType,String id){
       Boolean flag=false;
       try {
           affairConfiguerDao.updateById(isUsed,chColName,searchType,id);
           flag=true;
       }catch (Exception e){
           e.printStackTrace();
           flag=false;
       }
       return flag;
    }

    public List<AffairConfiguer> list(String flowId){

        List<AffairConfiguer> list = affairConfiguerDao.findAffairConfiguerByFlowId(flowId);

        return list;
    }
}

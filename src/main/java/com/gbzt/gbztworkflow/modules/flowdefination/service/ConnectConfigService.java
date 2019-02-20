package com.gbzt.gbztworkflow.modules.flowdefination.service;

import com.gbzt.gbztworkflow.modules.flowdefination.dao.ConnectConfigDao;
import com.gbzt.gbztworkflow.modules.flowdefination.entity.ConnectConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ConnectConfigService {
    @Autowired
    private ConnectConfigDao connectConfigDao;
    public List<ConnectConfig> list(){
        List<ConnectConfig> connectConfigList=connectConfigDao.findAll();
        return connectConfigList;
    }

    public Boolean deleteConfig(String id){
        boolean flag=false;
        try {
            connectConfigDao.deleteConnectConfigById(id);
            flag=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    public Boolean save(ConnectConfig connectConfig){
        boolean flag=false;

        try {
            connectConfigDao.save(connectConfig);
            flag=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}

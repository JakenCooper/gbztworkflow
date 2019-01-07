package com.gbzt.gbztworkflow.modules.workflowengine.mybatisservice;

import com.gbzt.gbztworkflow.modules.workflowengine.mybatisdao.TestUserDao;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TestUserService {

    @Autowired
    private TestUserDao testUserDao;

    @Transactional("dtm")
    public TestUser getTestUser(){
        return testUserDao.selectByPrimaryKey(1);
    }


    @Transactional(value = "dtm",readOnly = false)
    public void addTestUser(TestUser testUser){
        testUserDao.insert(testUser);
        if(testUser.getName().equals("我操")){
            throw new IllegalArgumentException("id wrong!");
        }
    }
}

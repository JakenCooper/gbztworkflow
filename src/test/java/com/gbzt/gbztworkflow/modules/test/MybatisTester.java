package com.gbzt.gbztworkflow.modules.test;

import com.gbzt.gbztworkflow.config.RootConfig;
import com.gbzt.gbztworkflow.modules.workflowengine.mybatisservice.TestUserService;
import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TestUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
public class MybatisTester {

    @Autowired
    private TestUserService testUserService;

    @Test
    public void gettestuser(){
        TestUser testUser = testUserService.getTestUser();
        System.out.println(testUser.getId()+" === "+testUser.getName());
        System.out.println(testUser);
    }

    @Test
    public void addtestuser(){
        TestUser testUser = new TestUser();
        testUser.setName("我操");
//        testUser.setName("Jaken");
        testUserService.addTestUser(testUser);
    }
}

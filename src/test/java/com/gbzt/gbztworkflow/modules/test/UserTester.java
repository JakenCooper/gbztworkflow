package com.gbzt.gbztworkflow.modules.test;

import com.gbzt.gbztworkflow.config.RootConfig;
import com.gbzt.gbztworkflow.consts.AppConst;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RootConfig.class)
public class UserTester {
    @Autowired
    private UserService userService;

    @Test
    public void adduser(){
        User user = new User();
        user.setId(3);
        user.setName("小白");
        user.setCreateTime(new Date());
        userService.addUser(user);
        assertNotNull(user.getId());
    }

    @Test
    public void getuser(){
        User user = userService.getUser(1);
        assertEquals("小白",user.getName());
    }

    @Test
    public void showconst(){
        System.out.println(AppConst.STDOUT_SWITCH);
    }
}

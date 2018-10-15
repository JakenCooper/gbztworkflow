package com.gbzt.gbztworkflow.modules.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service

public class UserService {

    @Autowired
    private UserDao userDao;

    @Transactional("jtm")
    public void addUser(User user){
        userDao.save(user);
    }

    public User getUser(Integer id){
        return userDao.findOne(id);
    }
}

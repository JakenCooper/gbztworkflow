package com.gbzt.gbztworkflow.modules.workflowengine.mybatisdao;


import com.gbzt.gbztworkflow.modules.workflowengine.pojo.TestUser;
import org.springframework.stereotype.Repository;

@Repository
public interface TestUserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TestUser record);

    int insertSelective(TestUser record);

    TestUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TestUser record);

    int updateByPrimaryKey(TestUser record);
}
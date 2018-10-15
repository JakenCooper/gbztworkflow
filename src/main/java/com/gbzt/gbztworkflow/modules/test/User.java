package com.gbzt.gbztworkflow.modules.test;

import com.gbzt.gbztworkflow.modules.base.BaseEntity;

import javax.persistence.*;

@Entity(name = "test_user")
public class User extends BaseEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="name")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

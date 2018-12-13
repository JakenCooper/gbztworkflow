package com.gbzt.gbztworkflow.modules.taskNodePermissions.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity(name="form_design_id_name")
public class TaskFormIdName {
    
    @Id
    @Column(name="id")
    private String id;
    
    //formid
    @Column(name="form_Id")
    private String formId;
    
    //form_name
    @Column(name="form_Name")
    private String formName;

    @Column(name = "create_date")
    private Date createDate;

    //备注
    @Column(name = "remark")
    private String remark;

    //删除标记
    @Column(name = "del_tag")
    private String deltag;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeltag() {
        return deltag;
    }

    public void setDeltag(String deltag) {
        this.deltag = deltag;
    }
}

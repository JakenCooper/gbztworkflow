/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.gbzt.gbztworkflow.modules.formDesign.entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;


/**
 * 表单设计列表Entity
 * @author 陈宏
 * @version 2018-10-08
 */
@Entity(name = "form_design")
public class FormDesign implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
    @Column(name = "id")
	private String id;
	@Column(name = "form_name")
	private String formName;
	@Column(name = "remark")
	private String remark;
	@Column(name = "delflag")
	private String delflag;
	@Column(name = "form_html")
    private String formHtml;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name="jsp_code")
    private String jspCode;
    @Column(name="currentFlowId")
    private String currentFlowId;
    @Column(name="jsp_code_view")
    private String jspCodeView;
    @Column(name = "container_width")
    private String containerWidth;
    @Column(name = "color")
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getJspCodeView() {
        return jspCodeView;
    }

    public void setJspCodeView(String jspCodeView) {
        this.jspCodeView = jspCodeView;
    }

    public String getCurrentFlowId() {
        return currentFlowId;
    }

    public void setCurrentFlowId(String currentFlowId) {
        this.currentFlowId = currentFlowId;
    }

    public String getJspCode() {
        return jspCode;
    }

    public void setJspCode(String jspCode) {
        this.jspCode = jspCode;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getFormHtml() {
        return formHtml;
    }

    public void setFormHtml(String formHtml) {
        this.formHtml = formHtml;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Length(min=0, max=255, message="表单名称长度必须介于 0 和 255 之间")
	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	
	@Length(min=0, max=255, message="备注长度必须介于 0 和 255 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Length(min=0, max=255, message="删除标记长度必须介于 0 和 255 之间")
	public String getDelflag() {
		return delflag;
	}

	public void setDelflag(String delflag) {
		this.delflag = delflag;
	}

    public String getContainerWidth() {
        return containerWidth;
    }

    public void setContainerWidth(String containerWidth) {
        this.containerWidth = containerWidth;
    }
}
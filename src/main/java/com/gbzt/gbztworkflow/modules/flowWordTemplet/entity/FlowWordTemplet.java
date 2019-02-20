/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.gbzt.gbztworkflow.modules.flowWordTemplet.entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;


/**
 * word打印模板列表Entity
 * @author wlh
 * @version 2019-01-22
 */
@Entity(name = "flow_word_templet")
public class FlowWordTemplet implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
    @Column(name = "id")
	private String id;
	@Column(name = "flow_id")
	private String flowId;
	@Column(name = "upload_user_ip")
	private String uploadUserIp;
	@Column(name = "word_templet_name")
	private String wordTempletName;
	@Column(name = "word_templet_address")
    private String wordTempletAddress;
    @Column(name = "del_flag")
    private String delFlag;
    
    @Length(min=0, max=64, message="id长度必须介于 0 和 64 之间")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @Length(min=0, max=64, message="id长度必须介于 0 和 64 之间")
    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }
    @Length(min=0, max=64, message="ip长度必须介于 0 和 64 之间")
    public String getUploadUserIp() {
        return uploadUserIp;
    }

    public void setUploadUserIp(String uploadUserIp) {
        this.uploadUserIp = uploadUserIp;
    }
    @Length(min=0, max=255, message="模板名称长度必须介于 0 和 255 之间")
    public String getWordTempletName() {
        return wordTempletName;
    }
    public void setWordTempletName(String wordTempletName) {
        this.wordTempletName = wordTempletName;
    }

    @Length(min=0, max=255, message="模板地址长度必须介于 0 和 255 之间")
    public String getWordTempletAddress() {
        return wordTempletAddress;
    }

    public void setWordTempletAddress(String wordTempletAddress) {
        this.wordTempletAddress = wordTempletAddress;
    }

    @Length(min=0, max=1, message="删除标记长度必须介于 0 和 1 之间")
    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}
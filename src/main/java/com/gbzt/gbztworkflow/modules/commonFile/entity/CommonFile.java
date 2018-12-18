package com.gbzt.gbztworkflow.modules.commonFile.entity;

/*
* 文件上传
*/

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "common_file")
public class CommonFile implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_realUrl")
    private String fileRealUrl;
    @Column(name = "file_url")
    private String fileUrl;
    @Column(name = "procInsId")
    private String procInsId;
    @Column(name = "flow_name")
    private String flowName;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "upload_by")
    private String uploadBy;
    @Column(name = "del_flag")
    private String delFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileRealUrl() {
        return fileRealUrl;
    }

    public void setFileRealUrl(String fileRealUrl) {
        this.fileRealUrl = fileRealUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getProcInsId() {
        return procInsId;
    }

    public void setProcInsId(String procInsId) {
        this.procInsId = procInsId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUploadBy() {
        return uploadBy;
    }

    public void setUploadBy(String uploadBy) {
        this.uploadBy = uploadBy;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}

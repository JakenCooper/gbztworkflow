package com.gbzt.gbztworkflow.modules.affairConfiguer.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
/**
 *事务查询Entity
 * @author 陈宏
 * @version 2018-12-4
 */

@Entity(name = "affair_configuer")
public class AffairConfiguer  implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "flow_id")
    private String flowId;
    @Column(name = "ch_col_name")
    private String chColName;
    @Column(name = "col_name")
    private String colName;
    @Column(name = "is_used")
    private String isUsed;
    @Column(name = "search_type")
    private String searchType;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "del_flag")
    private String delFlag;
    @Column(name = "remarks")
    private String remarks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getChColName() {
        return chColName;
    }

    public void setChColName(String chColName) {
        this.chColName = chColName;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(String isUsed) {
        this.isUsed = isUsed;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

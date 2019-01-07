package com.gbzt.gbztworkflow.modules.flowElement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity(name="flow_element_table")
public class FlowElement  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    
    @Column(name = "flow_id")
    private String flowId;
    
    @Column(name = "flow_name")
    private String flowName;
    
    @Column(name = "element_id")
    private String elementId;
    
    @Column(name = "element_name_cn")
    private String elementNameCn;
    
    @Column(name = "original_name")
    private String originalName;
    
    @Column(name = "hump_name")
    private String humpName;
    
    @Column(name = "create_date")
    private Date createDate;

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

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getElementNameCn() {
        return elementNameCn;
    }

    public void setElementNameCn(String elementNameCn) {
        this.elementNameCn = elementNameCn;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getHumpName() {
        return humpName;
    }

    public void setHumpName(String humpName) {
        this.humpName = humpName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public FlowElement(String id, String flowId, String flowName, String elementId, String elementNameCn, String originalName, String humpName, Date createDate) {
        this.id = id;
        this.flowId = flowId;
        this.flowName = flowName;
        this.elementId = elementId;
        this.elementNameCn = elementNameCn;
        this.originalName = originalName;
        this.humpName = humpName;
        this.createDate = createDate;
    }

    public FlowElement() {
    }
}



package com.gbzt.gbztworkflow.modules.workFlowPage.entity;
/*
*2019-1-4
*数据库分页查询传参数
* */
public class Page {
    private Integer pageTotal;//总页数
    private Integer cuurentPage;//当前页
    private Integer maxPageSize;//当前页显示最大条数
    private Integer dataTotal;//数据总数
    private Integer sortTyle;//排序方式

    public Integer getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(Integer pageTotal) {
        this.pageTotal = pageTotal;
    }

    public Integer getCuurentPage() {
        return cuurentPage;
    }

    public void setCuurentPage(Integer cuurentPage) {
        this.cuurentPage = cuurentPage;
    }

    public Integer getMaxPageSize() {
        return maxPageSize;
    }

    public void setMaxPageSize(Integer maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    public Integer getDataTotal() {
        return dataTotal;
    }

    public void setDataTotal(Integer dataTotal) {
        this.dataTotal = dataTotal;
    }

    public Integer getSortTyle() {
        return sortTyle;
    }

    public void setSortTyle(Integer sortTyle) {
        this.sortTyle = sortTyle;
    }
}

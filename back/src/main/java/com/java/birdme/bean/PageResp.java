package com.java.birdme.bean;

public class PageResp {
    private int offset;
    private int pageSize;
    private int totalCount;
    private int totalPage;
    private Object data;

    public PageResp(int offset, int pageSize, int totalCount, Object data) {
        this.offset = offset;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = totalCount%pageSize==0?totalCount/pageSize:totalCount/pageSize+1;
        this.data = data;
    }

    public static PageResp page(int offset, int pageSize, int totalCount, Object data){
        return new PageResp(offset, pageSize, totalCount, data);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage() {
        this.totalPage = totalCount%pageSize==0?totalCount/pageSize:totalCount/pageSize+1;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

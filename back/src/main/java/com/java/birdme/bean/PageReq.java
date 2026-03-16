package com.java.birdme.bean;

import java.io.Serializable;

public class PageReq<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int offset;
    private int pageSize;
    private T data;

    public PageReq() {
    }

    public PageReq(int offset, int pageSize, T data) {
        this.offset = offset;
        this.pageSize = pageSize;
        this.data = data;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

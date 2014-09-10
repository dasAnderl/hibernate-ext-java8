package com.anderl.hibernate.ext;


public class PagingHelper {

    private int pageSize = 20;
    private int firstResultIndex = 0;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getFirstResultIndex() {
        return firstResultIndex;
    }

    public void setFirstResultIndex(int firstResultIndex) {
        this.firstResultIndex = firstResultIndex;
    }
}

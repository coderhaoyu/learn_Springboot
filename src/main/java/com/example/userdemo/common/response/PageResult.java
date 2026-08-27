package com.example.userdemo.common.response;

import java.util.List;

/**
 * 分页响应结构，所有分页接口统一使用。
 *
 * @param <T> 列表元素类型，如 UserVo
 */
public class PageResult<T> {

    /** 当前页数据 */
    private final List<T> list;

    /** 符合条件的总条数 */
    private final long total;

    /** 当前页码，从 1 开始 */
    private final int page;

    /** 每页条数 */
    private final int size;

    /** 总页数，由 total 和 size 推导得出 */
    private final int totalPages;

    public PageResult(List<T> list, long total, int page, int size) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = (int) ((total + size - 1) / size);
    }

    public List<T> getList() {
        return list;
    }

    public long getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int getTotalPages() {
        return totalPages;
    }
}

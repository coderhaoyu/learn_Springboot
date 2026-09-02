package com.example.ouradventure.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class UserPageQueryRequest {

    @Min(value = 1, message = "页码最小为1")
    @Max(value = 10000,message = "页码最大为10000")
    private int page = 1;

    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 100, message = "每页数量最大为100")
    private int size = 10;


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

package com.example.demo.interceptor;

import lombok.Data;

@Data
public class PageParam {
    private int page;
    private int pageSize;
    private boolean useFlag;
    private boolean checkFlag;
    private int total ;
    private int totalPage;

}

package com.example.demo.filter;

import java.util.ArrayList;
import java.util.List;

public class FilterChain implements Filter {
    private List<Filter> filters = new ArrayList<Filter>();
    private int index;
    public FilterChain addFilter(Filter f){
        filters.add(f);
        return  this;
    }

    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        if(index == filters.size())return;
        Filter filter = filters.get(index);
        index++;
        filter.doFilter(request,response,this);
    }
}

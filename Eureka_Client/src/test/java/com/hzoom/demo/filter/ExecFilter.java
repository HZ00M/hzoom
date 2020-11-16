package com.hzoom.demo.filter;

public class ExecFilter implements Filter{

    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        request.setContent(request.getContent()+"-------Exec--------");
        chain.doFilter(request,response,chain);
        response.setContent(response.getContent()+"---------Exec--------");
    }
}

package com.example.demo.filter;

public class HtmlFilter implements Filter{

    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        request.setContent(request.getContent().replace("敏感","***"));
        chain.doFilter(request,response,chain);
        response.setContent(response.getContent().replace("敏感","***"));
    }
}

package com.mycompany.app;

public class FrontendSearchResponse {
    private String query;
    private int resultCount;
    
    public FrontendSearchResponse(String query, int resultCount) {
        this.query = query;
        this.resultCount = resultCount;
    }
    
    public String getQuery() {
        return query;
    }
    
    public int getResultCount() {
        return resultCount;
    }
}
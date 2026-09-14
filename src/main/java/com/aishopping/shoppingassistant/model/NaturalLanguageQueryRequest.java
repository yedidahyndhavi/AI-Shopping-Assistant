package com.aishopping.shoppingassistant.model;

public class NaturalLanguageQueryRequest {

    private String query;

    public NaturalLanguageQueryRequest() {
    }

    public NaturalLanguageQueryRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
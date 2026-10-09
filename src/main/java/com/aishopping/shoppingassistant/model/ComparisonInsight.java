package com.aishopping.shoppingassistant.model;

import java.util.List;

public class ComparisonInsight {

    private String product1Name;
    private String product2Name;
    private String overallWinner;
    private String summary;

    private List<String> product1Strengths;
    private List<String> product2Strengths;
    private List<String> tradeOffs;

    public ComparisonInsight() {
    }

    public ComparisonInsight(
            String product1Name,
            String product2Name,
            String overallWinner,
            String summary,
            List<String> product1Strengths,
            List<String> product2Strengths,
            List<String> tradeOffs) {

        this.product1Name = product1Name;
        this.product2Name = product2Name;
        this.overallWinner = overallWinner;
        this.summary = summary;
        this.product1Strengths = product1Strengths;
        this.product2Strengths = product2Strengths;
        this.tradeOffs = tradeOffs;
    }

    public String getProduct1Name() {
        return product1Name;
    }

    public void setProduct1Name(String product1Name) {
        this.product1Name = product1Name;
    }

    public String getProduct2Name() {
        return product2Name;
    }

    public void setProduct2Name(String product2Name) {
        this.product2Name = product2Name;
    }

    public String getOverallWinner() {
        return overallWinner;
    }

    public void setOverallWinner(String overallWinner) {
        this.overallWinner = overallWinner;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getProduct1Strengths() {
        return product1Strengths;
    }

    public void setProduct1Strengths(
            List<String> product1Strengths) {

        this.product1Strengths = product1Strengths;
    }

    public List<String> getProduct2Strengths() {
        return product2Strengths;
    }

    public void setProduct2Strengths(
            List<String> product2Strengths) {

        this.product2Strengths = product2Strengths;
    }

    public List<String> getTradeOffs() {
        return tradeOffs;
    }

    public void setTradeOffs(
            List<String> tradeOffs) {

        this.tradeOffs = tradeOffs;
    }
}
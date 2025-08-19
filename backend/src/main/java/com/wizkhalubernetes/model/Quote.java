package com.wizkhalubernetes.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quotes")
public class Quote {
    @Id
    private String id;
    private String quote;
    private String timestamp;
    private String ip;
    private int quoteNumber;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getQuote() { return quote; }
    public void setQuote(String quote) { this.quote = quote; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public int getQuoteNumber() { return quoteNumber; }
    public void setQuoteNumber(int quoteNumber) { this.quoteNumber = quoteNumber; }
}

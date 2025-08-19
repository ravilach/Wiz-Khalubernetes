package com.wizkhalubernetes.model.h2;

import jakarta.persistence.*;

@Entity
@Table(name = "quotes")
public class QuoteH2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String quote;
    private String timestamp;
    private String ip;
    private int quoteNumber;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuote() { return quote; }
    public void setQuote(String quote) { this.quote = quote; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public int getQuoteNumber() { return quoteNumber; }
    public void setQuoteNumber(int quoteNumber) { this.quoteNumber = quoteNumber; }
}

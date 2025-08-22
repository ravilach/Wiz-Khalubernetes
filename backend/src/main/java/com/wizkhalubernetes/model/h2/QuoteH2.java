package com.wizkhalubernetes.model.h2;

import jakarta.persistence.*;

/**
 * Data model for Wiz Khalifa quotes stored in H2 database.
 * Contains quote text, timestamp, IP, and quote number.
 */
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

    /**
     * Gets the unique ID of the quote.
     * @return quote ID
     */
    public Long getId() { return id; }
    /**
     * Sets the unique ID of the quote.
     * @param id quote ID
     */
    public void setId(Long id) { this.id = id; }
    /**
     * Gets the quote text.
     * @return quote string
     */
    public String getQuote() { return quote; }
    /**
     * Sets the quote text.
     * @param quote quote string
     */
    public void setQuote(String quote) { this.quote = quote; }
    /**
     * Gets the timestamp when the quote was submitted.
     * @return timestamp string
     */
    public String getTimestamp() { return timestamp; }
    /**
     * Sets the timestamp when the quote was submitted.
     * @param timestamp timestamp string
     */
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    /**
     * Gets the IP address of the submitter.
     * @return IP address string
     */
    public String getIp() { return ip; }
    /**
     * Sets the IP address of the submitter.
     * @param ip IP address string
     */
    public void setIp(String ip) { this.ip = ip; }
    /**
     * Gets the quote number (sequence).
     * @return quote number
     */
    public int getQuoteNumber() { return quoteNumber; }
    /**
     * Sets the quote number (sequence).
     * @param quoteNumber quote number
     */
    public void setQuoteNumber(int quoteNumber) { this.quoteNumber = quoteNumber; }
}

// REST API controller for quote operations and node info
package com.wizkhalubernetes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.wizkhalubernetes.model.Quote;
import com.wizkhalubernetes.repository.QuoteRepository;
import com.wizkhalubernetes.repository.QuoteJpaRepository;
import org.springframework.dao.DataAccessResourceFailureException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QuoteController {
    @Autowired(required = false)
    private QuoteRepository quoteRepository;
    @Autowired(required = false)
    private QuoteJpaRepository quoteJpaRepository;
    @Autowired
    private org.springframework.core.env.Environment env;

    /**
     * Adds a new quote and captures the user's IP address from the HTTP request.
     */
    @PostMapping("/quotes")
    public ResponseEntity<?> addQuote(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        boolean useMongo = Boolean.parseBoolean(env.getProperty("REMOTE_DB", "false"));
        if (useMongo) {
            if (quoteRepository == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(errorResponse("MongoDB connection unavailable at configured URL."));
            }
            try {
                String quoteText = payload.get("quote");
                Quote quote = new Quote();
                quote.setQuote(quoteText);
                quote.setTimestamp(Instant.now().toString());
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty()) {
                    ip = request.getRemoteAddr();
                }
                quote.setIp(ip);
                quote.setQuoteNumber(getNextQuoteNumber());
                Quote saved = quoteRepository.save(quote);
                return ResponseEntity.ok(saved);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Failed to save quote: " + e.getMessage()));
            }
        } else {
            if (quoteJpaRepository == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(errorResponse("H2/JPA repository unavailable."));
            }
            try {
                String quoteText = payload.get("quote");
                Quote quote = new Quote();
                quote.setQuote(quoteText);
                quote.setTimestamp(Instant.now().toString());
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty()) {
                    ip = request.getRemoteAddr();
                }
                quote.setIp(ip);
                quote.setQuoteNumber(getNextQuoteNumber());
                Quote saved = quoteJpaRepository.save(quote);
                return ResponseEntity.ok(saved);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Failed to save quote: " + e.getMessage()));
            }
        }
    }


    @GetMapping("/quotes/latest")
    public ResponseEntity<?> getLatestQuote() {
        if (quoteRepository == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse("MongoDB connection unavailable at configured URL."));
        }
        try {
            long count = quoteRepository.count();
            if (count == 0) {
                return ResponseEntity.ok().body(null);
            }
            Quote latest = quoteRepository.findAll()
                .stream()
                .max((a, b) -> Integer.compare(a.getQuoteNumber(), b.getQuoteNumber()))
                .orElse(null);
            return ResponseEntity.ok(latest);
        } catch (DataAccessResourceFailureException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse("MongoDB connection unavailable at configured URL."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Unexpected error: " + ex.getMessage()));
        }
    }

    @GetMapping("/nodeinfo")
    public Map<String, Object> getNodeInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("hostname", getHostName());
        info.put("app", "Wiz Khalubernetes");
        info.put("os.name", System.getProperty("os.name"));
        info.put("os.version", System.getProperty("os.version"));
        info.put("os.arch", System.getProperty("os.arch"));
        info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        info.put("maxMemoryMB", Runtime.getRuntime().maxMemory() / (1024 * 1024));
        info.put("totalMemoryMB", Runtime.getRuntime().totalMemory() / (1024 * 1024));
        info.put("freeMemoryMB", Runtime.getRuntime().freeMemory() / (1024 * 1024));
        info.put("timestamp", Instant.now().toString());
        // Add more Docker/container-specific info if needed
        return info;
    }

    private Map<String, String> errorResponse(String msg) {
        Map<String, String> err = new HashMap<>();
        err.put("error", msg);
        return err;
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * Returns the next quote number based on the active DB.
     */
    private int getNextQuoteNumber() {
        boolean useMongo = Boolean.parseBoolean(env.getProperty("REMOTE_DB", "false"));
        if (useMongo && quoteRepository != null) {
            return (int) (quoteRepository.count() + 1);
        } else if (quoteJpaRepository != null) {
            return (int) (quoteJpaRepository.count() + 1);
        } else {
            return 1;
        }
    }
}

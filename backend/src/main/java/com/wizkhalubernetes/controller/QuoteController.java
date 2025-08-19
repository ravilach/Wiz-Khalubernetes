// REST API controller for quote operations and node info
package com.wizkhalubernetes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.wizkhalubernetes.model.mongo.QuoteMongo;
import com.wizkhalubernetes.model.h2.QuoteH2;
import com.wizkhalubernetes.repository.mongo.QuoteMongoRepository;
import com.wizkhalubernetes.repository.h2.QuoteH2Repository;
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
    @org.springframework.beans.factory.annotation.Qualifier("mongoQuoteRepository")
    private QuoteMongoRepository quoteMongoRepository;
    @Autowired(required = false)
    @org.springframework.beans.factory.annotation.Qualifier("h2QuoteRepository")
    private QuoteH2Repository quoteJpaRepository;
    @Autowired
    private org.springframework.core.env.Environment env;

    /**
     * Adds a new quote and captures the user's IP address from the HTTP request.
     */
    @PostMapping("/quotes")
    public ResponseEntity<?> addQuote(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        boolean useMongo = Boolean.parseBoolean(env.getProperty("REMOTE_DB", "false"));
        if (useMongo) {
            if (quoteMongoRepository == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(errorResponse("MongoDB connection unavailable at configured URL."));
            }
            try {
                String quoteText = payload.get("quote");
                QuoteMongo quote = new QuoteMongo();
                quote.setQuote(quoteText);
                quote.setTimestamp(Instant.now().toString());
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty()) {
                    ip = request.getRemoteAddr();
                }
                quote.setIp(ip);
                quote.setQuoteNumber(getNextQuoteNumber());
                try {
                    QuoteMongo saved = quoteMongoRepository.save(quote);
                    System.out.println("[INFO] Successfully saved quote to MongoDB: " + saved);
                    return ResponseEntity.ok(saved);
                } catch (Exception e) {
                    System.err.println("[ERROR] Failed to save quote to MongoDB: " + e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(errorResponse("Failed to save quote: " + e.getMessage()));
                }
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
                QuoteH2 quote = new QuoteH2();
                quote.setQuote(quoteText);
                quote.setTimestamp(Instant.now().toString());
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty()) {
                    ip = request.getRemoteAddr();
                }
                quote.setIp(ip);
                quote.setQuoteNumber(getNextQuoteNumber());
                QuoteH2 saved = quoteJpaRepository.save(quote);
                return ResponseEntity.ok(saved);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Failed to save quote: " + e.getMessage()));
            }
        }
    }


    @GetMapping("/quotes/latest")
    public ResponseEntity<?> getLatestQuote() {
    if (quoteMongoRepository == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse("MongoDB connection unavailable at configured URL."));
        }
        try {
            long count = quoteMongoRepository.count();
            System.out.println("[INFO] MongoDB quote count: " + count);
            if (count == 0) {
                return ResponseEntity.ok().body(null);
            }
            QuoteMongo latest = null;
            try {
                latest = quoteMongoRepository.findAll()
                    .stream()
                    .max((a, b) -> Integer.compare(a.getQuoteNumber(), b.getQuoteNumber()))
                    .orElse(null);
                System.out.println("[INFO] Fetched latest quote from MongoDB: " + latest);
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to fetch latest quote from MongoDB: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Failed to fetch latest quote: " + e.getMessage()));
            }
            return ResponseEntity.ok(latest);
        } catch (DataAccessResourceFailureException ex) {
            System.err.println("[ERROR] MongoDB connection unavailable: " + ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse("MongoDB connection unavailable at configured URL."));
        } catch (Exception ex) {
            System.err.println("[ERROR] Unexpected error during MongoDB read: " + ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Unexpected error: " + ex.getMessage()));
        }
    }

    /**
     * Returns all quotes from the active DB (H2 or Mongo).
     */
    @GetMapping("/quotes")
    public ResponseEntity<?> getAllQuotes() {
        boolean useMongo = Boolean.parseBoolean(env.getProperty("REMOTE_DB", "false"));
        if (useMongo) {
            if (quoteMongoRepository == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(errorResponse("MongoDB connection unavailable at configured URL."));
            }
            try {
                return ResponseEntity.ok(quoteMongoRepository.findAll());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Failed to fetch quotes: " + e.getMessage()));
            }
        } else {
            if (quoteJpaRepository == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(errorResponse("H2/JPA repository unavailable."));
            }
            try {
                return ResponseEntity.ok(quoteJpaRepository.findAll());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse("Failed to fetch quotes: " + e.getMessage()));
            }
        }
    }

    /**
     * Deletes a quote by ID from the active DB (H2 or Mongo).
     */
    @DeleteMapping("/quotes/{id}")
    public ResponseEntity<?> deleteQuote(@PathVariable("id") String id) {
        boolean useMongo = Boolean.parseBoolean(env.getProperty("REMOTE_DB", "false"));
        try {
            if (useMongo) {
                if (quoteMongoRepository == null) {
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(errorResponse("MongoDB connection unavailable at configured URL."));
                }
                quoteMongoRepository.deleteById(Long.parseLong(id));
                return ResponseEntity.ok().body("Deleted");
            } else {
                if (quoteJpaRepository == null) {
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(errorResponse("H2/JPA repository unavailable."));
                }
                quoteJpaRepository.deleteById(Long.parseLong(id));
                return ResponseEntity.ok().body("Deleted");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Failed to delete quote: " + e.getMessage()));
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

    /**
     * Returns the current DB status (H2 or Mongo) for frontend UX.
     */
    @GetMapping("/dbstatus")
    public Map<String, String> getDbStatus() {
        boolean useMongo = Boolean.parseBoolean(env.getProperty("REMOTE_DB", "false"));
        Map<String, String> status = new HashMap<>();
        if (useMongo) {
            status.put("type", "MongoDB");
            status.put("connected", quoteMongoRepository != null ? "true" : "false");
            status.put("message", quoteMongoRepository != null ? "Connected to MongoDB" : "MongoDB repository unavailable");
        } else {
            status.put("type", "H2");
            status.put("connected", quoteJpaRepository != null ? "true" : "false");
            status.put("message", quoteJpaRepository != null ? "Connected to H2" : "H2 repository unavailable");
        }
        return status;
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
        if (useMongo && quoteMongoRepository != null) {
            return (int) (quoteMongoRepository.count() + 1);
    } else if (quoteJpaRepository != null) {
            return (int) (quoteJpaRepository.count() + 1);
        } else {
            return 1;
        }
    }
}

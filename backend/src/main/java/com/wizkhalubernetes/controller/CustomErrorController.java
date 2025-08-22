// Custom error controller for handling /error endpoint
package com.wizkhalubernetes.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

/**
 * Custom error controller for handling /error endpoint.
 * Returns friendly error messages for failed requests.
 */
@RestController
public class CustomErrorController implements ErrorController {
    @Autowired
    private ErrorAttributes errorAttributes;

    /**
     * Handles errors routed to /error endpoint.
     * @param webRequest WebRequest context
     * @return ResponseEntity with error details and HTTP 500 status
     */
    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(WebRequest webRequest) {
        Map<String, Object> errors = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        errors.put("message", "Something went wrong. Please check your request or try again later.");
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

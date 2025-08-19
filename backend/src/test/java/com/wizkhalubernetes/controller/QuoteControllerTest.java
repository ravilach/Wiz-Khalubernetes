package com.wizkhalubernetes.controller;

import com.wizkhalubernetes.model.Quote;
import com.wizkhalubernetes.repository.QuoteRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QuoteControllerTest {
    @Test
    void testAddQuoteWithMockedRepo() {
        QuoteRepository mockRepo = Mockito.mock(QuoteRepository.class);
        Mockito.when(mockRepo.count()).thenReturn(0L);
        Mockito.when(mockRepo.save(Mockito.any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QuoteController controller = new QuoteController();
        // Use reflection to inject mock (since @Autowired is not used in test)
        try {
            java.lang.reflect.Field repoField = QuoteController.class.getDeclaredField("quoteRepository");
            repoField.setAccessible(true);
            repoField.set(controller, mockRepo);
        } catch (Exception e) {
            fail("Failed to inject mock repository");
        }

        Map<String, String> payload = new HashMap<>();
        payload.put("quote", "Test quote");
    jakarta.servlet.http.HttpServletRequest mockRequest = Mockito.mock(jakarta.servlet.http.HttpServletRequest.class);
    ResponseEntity<?> response = controller.addQuote(payload, mockRequest);
    assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
    Object body = response.getBody();
    assertNotNull(body);
    assertTrue(body instanceof Quote);
    assertEquals("Test quote", ((Quote) body).getQuote());
    }
}

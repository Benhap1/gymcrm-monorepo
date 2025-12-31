package com.gymcrm.gym_crm_spring.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionLoggingFilter extends HttpFilter {

    private static final String TRANSACTION_ID = "transactionId";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            String transactionId = request.getHeader(TRANSACTION_ID);
            if (transactionId == null || transactionId.isBlank()) {
                transactionId = UUID.randomUUID().toString();
            }

            MDC.put(TRANSACTION_ID, transactionId);

            response.setHeader(TRANSACTION_ID, transactionId);

            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
    }
}

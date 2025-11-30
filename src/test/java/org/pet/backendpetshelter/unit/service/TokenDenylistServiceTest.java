package org.pet.backendpetshelter.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.Service.TokenDenylistService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TokenDenylistService Tests")
class TokenDenylistServiceTest {

    private TokenDenylistService denylistService;

    @BeforeEach
    void setUp() {
        denylistService = new TokenDenylistService();
    }

    @Nested
    @DisplayName("Token Denylist Tests - deny() & isDenied()")
    class TokenDenylistTests {

        // ==================== EQUIVALENCE PARTITIONING - VALID PARTITION ====================
        
        @Test
        @DisplayName("Should return false when token is not in denylist")
        void testTokenNotInDenylist() {
            String token = "valid.jwt.token";

            boolean isDenied = denylistService.isDenied(token);

            assertFalse(isDenied);
        }

        // ==================== EQUIVALENCE PARTITIONING - INVALID PARTITION ====================
        
        @Test
        @DisplayName("Should return true when token is added to denylist")
        void testTokenAddedToDenylist() {
            String token = "denied.jwt.token";
            long ttl = 3600L;

            denylistService.deny(token, ttl);
            boolean isDenied = denylistService.isDenied(token);

            assertTrue(isDenied);
        }

        // ==================== BOUNDARY VALUE ANALYSIS ====================
        
        @Test
        @DisplayName("Should deny token just added (boundary: immediately after adding)")
        void testTokenJustAdded() {
            String token = "just.added.token";

            denylistService.deny(token, 3600L);

            // Token should be denied immediately after adding
            assertTrue(denylistService.isDenied(token));
        }

        @Test
        @DisplayName("Should remove token at expiration boundary")
        void testTokenAtExpirationBoundary() throws InterruptedException {
            String token = "boundary.token";

            denylistService.deny(token, 2L); // 2 seconds TTL
            
            // Before expiration - should be denied
            Thread.sleep(1000);
            assertTrue(denylistService.isDenied(token));
            
            // After expiration - should not be denied
            Thread.sleep(2000);
            assertFalse(denylistService.isDenied(token));
        }

        @Test
        @DisplayName("Should remove token past expiration")
        void testTokenPastExpiration() throws InterruptedException {
            String token = "expired.token";

            denylistService.deny(token, 1L); // 1 second TTL
            Thread.sleep(2000); // Wait past expiration

            // Token should not be denied after expiration
            assertFalse(denylistService.isDenied(token));
        }

        // ==================== AUTOMATIC CLEANUP TESTS ====================
        
        @Test
        @DisplayName("Should automatically clean up expired tokens")
        void testExpiredTokensAutomaticallyCleanedUp() throws InterruptedException {
            String expiredToken = "expired.token";
            String validToken = "valid.token";

            denylistService.deny(expiredToken, 1L); // 1 second TTL
            denylistService.deny(validToken, 3600L); // 1 hour TTL
            
            Thread.sleep(2000); // Wait for first token to expire

            // isDenied() should trigger cleanup and remove expired token
            assertFalse(denylistService.isDenied(expiredToken));
            // Valid token should still be denied
            assertTrue(denylistService.isDenied(validToken));
        }

        @Test
        @DisplayName("Should remove expired entries when isDenied() is called")
        void testIsDeniedRemovesExpiredEntries() throws InterruptedException {
            String token = "cleanup.token";

            denylistService.deny(token, 1L);
            Thread.sleep(2000);

            // First call should detect expiration and remove it
            assertFalse(denylistService.isDenied(token));
            
            // Second call should still return false (token was removed)
            assertFalse(denylistService.isDenied(token));
        }

        // ==================== THREAD SAFETY TEST ====================
        
        @Test
        @DisplayName("Should handle concurrent access in a thread-safe manner")
        void testConcurrentAccessIsThreadSafe() throws InterruptedException {
            int threadCount = 10;
            int operationsPerThread = 100;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);

            // Half threads deny tokens, half check tokens
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < operationsPerThread; j++) {
                            String token = "token-" + threadId + "-" + j;
                            if (threadId % 2 == 0) {
                                // Even threads: deny tokens
                                denylistService.deny(token, 3600L);
                            } else {
                                // Odd threads: check tokens from previous even thread
                                if (denylistService.isDenied("token-" + (threadId - 1) + "-" + j)) {
                                    successCount.incrementAndGet();
                                }
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await(10, TimeUnit.SECONDS);
            executor.shutdown();

            // Verify all deny operations worked
            for (int i = 0; i < threadCount; i += 2) {
                for (int j = 0; j < operationsPerThread; j++) {
                    assertTrue(denylistService.isDenied("token-" + i + "-" + j));
                }
            }
        }
    }
}
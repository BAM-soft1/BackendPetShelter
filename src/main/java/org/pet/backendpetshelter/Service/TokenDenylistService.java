package org.pet.backendpetshelter.Service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenDenylistService {
    private final Map<String, Long> denylist = new ConcurrentHashMap<>();

    public void deny(String token, long ttlSeconds) {
        denylist.put(token, Instant.now().getEpochSecond() + ttlSeconds);
    }

    public boolean isDenied(String token) {
        Long until = denylist.get(token);
        if (until == null) return false;
        if (Instant.now().getEpochSecond() > until) {
            denylist.remove(token);
            return false;
        }
        return true;
    }
}
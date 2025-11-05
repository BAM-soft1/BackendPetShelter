package org.pet.backendpetshelter.Configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    private String accessSecret;
    private String refreshSecret;
    private long accessExpirationSeconds;
    private long refreshExpirationSeconds;

    public String getAccessSecret() { return accessSecret; }
    public void setAccessSecret(String accessSecret) { this.accessSecret = accessSecret; }

    public String getRefreshSecret() { return refreshSecret; }
    public void setRefreshSecret(String refreshSecret) { this.refreshSecret = refreshSecret; }

    public long getAccessExpirationSeconds() { return accessExpirationSeconds; }
    public void setAccessExpirationSeconds(long accessExpirationSeconds) { this.accessExpirationSeconds = accessExpirationSeconds; }

    public long getRefreshExpirationSeconds() { return refreshExpirationSeconds; }
    public void setRefreshExpirationSeconds(long refreshExpirationSeconds) { this.refreshExpirationSeconds = refreshExpirationSeconds; }
}
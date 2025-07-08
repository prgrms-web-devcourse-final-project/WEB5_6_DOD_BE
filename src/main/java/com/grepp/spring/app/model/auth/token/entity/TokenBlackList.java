package com.grepp.spring.app.model.auth.token.entity;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash("tokenBlackList")
public class TokenBlackList {
    @Id
    private String id;
    private String email;
    private String tokenId;
    private OffsetDateTime createdAt = OffsetDateTime.now();
    
    public TokenBlackList(String email, String tokenId) {
        this.id = email + ":" + tokenId;
        this.email = email;
        this.tokenId = tokenId;
    }
}

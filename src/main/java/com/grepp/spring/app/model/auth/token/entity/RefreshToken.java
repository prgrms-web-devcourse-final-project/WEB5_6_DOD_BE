package com.grepp.spring.app.model.auth.token.entity;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RefreshToken {
    private String id = UUID.randomUUID().toString();
    private String atId;
    private String token = UUID.randomUUID().toString();
    private Long ttl = 3600 * 24 * 7L;
    private Long expires;
    
    public RefreshToken(String atId) {
        this.atId = atId;
    }
}

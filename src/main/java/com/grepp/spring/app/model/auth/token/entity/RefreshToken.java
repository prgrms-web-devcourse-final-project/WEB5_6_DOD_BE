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
@NoArgsConstructor
public class RefreshToken {
    private String id = UUID.randomUUID().toString(); // 리프레시 토큰 id
    private String atId; // 연결된 엑세스 토큰의 JTI
    private Long ttl = 3600 * 24 * 7L;
}

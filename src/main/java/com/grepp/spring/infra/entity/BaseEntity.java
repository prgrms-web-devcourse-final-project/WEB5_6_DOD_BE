package com.grepp.spring.infra.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    
    protected Boolean activated = true;
    
    @CreatedDate
    protected LocalDateTime createdAt = LocalDateTime.now();
    @LastModifiedDate
    protected LocalDateTime modifiedAt = LocalDateTime.now();

    public void unActivated(){
        this.activated = false;
    }
}

package com.example.JWTlogin.Model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseModel {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @CreatedDate
    private String CreatedAt;
    @LastModifiedDate
    private String ModifyAt;
}

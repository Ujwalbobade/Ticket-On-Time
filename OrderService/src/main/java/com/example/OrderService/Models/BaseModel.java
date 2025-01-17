package com.example.OrderService.Models;



import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Document
public abstract class BaseModel {

    @MongoId
    private Long id;

    @CreatedDate
    @Field("createdAt") // Optional: Specify the field name in MongoDB
    private Date createdAt;

    @LastModifiedDate
    @Field("modifiedAt") // Optional: Specify the field name in MongoDB
    private Date modifiedAt;}


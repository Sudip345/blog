package com.example.demo.Operations.Entity;

import com.example.demo.Entity.Blog;
import com.example.demo.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "like")
public class Likes {
    @org.springframework.data.annotation.Id
    private ObjectId id ;
    @NonNull
    private String user;
    @NonNull
    private ObjectId blogID;
    private LocalDateTime localDateTime;
}

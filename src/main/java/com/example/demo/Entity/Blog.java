package com.example.demo.Entity;

import com.example.demo.Operations.Entity.Comments;
import com.example.demo.Operations.Entity.Likes;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "blog")
public class Blog {
    @org.springframework.data.annotation.Id
    private ObjectId id;
    private String author;
    private String title;
    @NonNull
    private String content;
    private LocalDateTime localDateTime;
    @DBRef
    private List<Likes> likes = new ArrayList<>();
    @DBRef
    private List<Comments> comments = new ArrayList<>();
}

package com.example.demo.Operations.Entity;

import com.example.demo.Entity.Blog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comment")
public class Comments {
    @Id
    private ObjectId commentId;
    private String stringID;
    private String comment;
    @DBRef
    private List<Comments> replies = new ArrayList<>();
    private int likes;
    private ObjectId blogID;
    private String author;
    private LocalDateTime time;
}

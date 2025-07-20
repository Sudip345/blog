package com.example.demo.Entity;

import com.example.demo.Operations.Entity.Comments;
import com.example.demo.Operations.Entity.Likes;
import com.mongodb.lang.NonNull;
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
@Document(collection = "blog")
public class Blog {
    @Id
    private ObjectId id;
    private String caption;
    private String author;
    private LocalDateTime localDateTime;
    private String stringID;
    @DBRef
    private List<Likes> likes = new ArrayList<>();
    @DBRef
    private List<Comments> comments = new ArrayList<>();

    private List<ObjectId> fileID = new ArrayList<>();
}

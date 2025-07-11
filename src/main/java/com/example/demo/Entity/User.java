package com.example.demo.Entity;

import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @org.springframework.data.annotation.Id
    private ObjectId userId;
    @NonNull
    @Indexed(unique = true)
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;
    private List<String> roles = new ArrayList<>();
    @DBRef
    private List<Blog> list=new ArrayList<>();

}

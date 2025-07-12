package com.example.demo.Repository;

import com.example.demo.Entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface UserRepository extends MongoRepository <User, ObjectId>{
    public Optional<User> findByUsername(String name);
    public void deleteByUsername(String name);
    Optional<User> findByUsernameOrEmail(String username, String email);
}

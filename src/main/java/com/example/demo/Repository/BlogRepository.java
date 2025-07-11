package com.example.demo.Repository;

import com.example.demo.Entity.Blog;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlogRepository extends MongoRepository<Blog, ObjectId> {
}

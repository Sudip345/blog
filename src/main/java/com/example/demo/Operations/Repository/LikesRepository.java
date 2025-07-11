package com.example.demo.Operations.Repository;

import com.example.demo.Operations.Entity.Likes;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface LikesRepository extends MongoRepository<Likes, ObjectId> {
}

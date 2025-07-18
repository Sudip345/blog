package com.example.demo.Operations.Repository;

import com.example.demo.Operations.Entity.Comments;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<Comments, ObjectId> {
}

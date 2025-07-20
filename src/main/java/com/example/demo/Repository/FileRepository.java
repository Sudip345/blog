package com.example.demo.Repository;

import com.example.demo.Entity.Files;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReadPreference;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends MongoRepository<Files, ObjectId> {
}

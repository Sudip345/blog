package com.example.demo.Operations.Service;

import com.example.demo.Operations.Entity.Likes;
import com.example.demo.Operations.Repository.LikesRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class LikesServices {
    @Autowired
    LikesRepository likesRepository;

    public Likes findLike(ObjectId id){
       return likesRepository.findById(id).orElse(null) ;
    }
    public Likes addLikes(Likes likes){
        likes.setLocalDateTime(LocalDateTime.now());
        return likesRepository.insert(likes);
    }
    public void removeLikes(ObjectId id){
        likesRepository.deleteById(id);
    }
    public void savelike(Likes likes){
        likesRepository.save(likes);
    }
}

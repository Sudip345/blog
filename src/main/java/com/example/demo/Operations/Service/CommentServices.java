package com.example.demo.Operations.Service;

import com.example.demo.Operations.Repository.CommentRepository;
import com.example.demo.Operations.Entity.Comments;
import com.example.demo.Service.UserServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServices {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserServices userServices;
    public Comments addComment(Comments comment) {
        return commentRepository.insert(comment);
    }

    public  void deleteComment( ObjectId id){
        commentRepository.deleteById(id);

    }

    public Comments UpdateComment( Comments comments,ObjectId id){
        Comments oldCmnt = commentRepository.findById(id).orElse(null);
        if(oldCmnt==null)
            return null ;
        oldCmnt.setComment(comments.getComment());
        oldCmnt.setTime(LocalDateTime.now());
        return commentRepository.save(oldCmnt);
    }

    public Comments findComment( ObjectId id){
        return commentRepository.findById(id).orElse(null);
    }

    public Comments saveComment(Comments comments){return  commentRepository.save(comments);}



}

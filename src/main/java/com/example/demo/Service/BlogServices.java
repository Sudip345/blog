package com.example.demo.Service;

import com.example.demo.Entity.Blog;
import com.example.demo.Entity.User;
import com.example.demo.Operations.Service.CommentServices;
import com.example.demo.Operations.Entity.Comments;
import com.example.demo.Operations.Entity.Likes;
import com.example.demo.Operations.Service.LikesServices;
import com.example.demo.Repository.BlogRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service

public class BlogServices {
    @Autowired
    BlogRepository blogRepository;
    @Autowired
    CommentServices commentServices;
    @Autowired
    LikesServices likesServices;
    @Autowired
    UserServices userServices;

    public Blog findBlog(ObjectId id){
        return blogRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean insertBlog(Blog blog){
        String user = blog.getAuthor();
        User user1 = userServices.findUser(user);
        if(user1==null)
            return false;
        blog.setLocalDateTime(LocalDateTime.now());
        blogRepository.insert(blog);
        return true;
    }
    @Transactional
    public boolean removeBlog(ObjectId id) {
        Blog temp = blogRepository.findById(id).orElse(null);
        if(temp==null)
            return false;

        List<Likes> likesList = temp.getLikes();
        List<Comments> commentsList = temp.getComments();
        for(Likes likes:likesList){
            likesServices.removeLikes(likes.getId());
        }
        for (Comments comments: commentsList){
            commentServices.deleteComment(comments.getCommentId());
        }
        blogRepository.deleteById(id);
        return true;
    }

    @Transactional
    public boolean saveBlog(Blog blog){
        blogRepository.save(blog);
        return true;
    }
}

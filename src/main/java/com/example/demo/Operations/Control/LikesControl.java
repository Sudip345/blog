package com.example.demo.Operations.Control;

import com.example.demo.Entity.Blog;
import com.example.demo.Operations.Entity.Likes;
import com.example.demo.Operations.Service.LikesServices;
import com.example.demo.Service.BlogServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class LikesControl {

    @Autowired
    BlogServices blogServices;

    @Autowired
    LikesServices likesServices;

    public Likes LikeBlog(Blog blog,Likes likes){
        likes.setBlogID(blog.getId());
        likes.setLocalDateTime(LocalDateTime.now());
        likesServices.addLikes(likes);
        blog.getLikes().add(likes);
        blogServices.saveBlog(blog);
        return likes;
    }

    public boolean deleteLike( ObjectId id){
        Likes likes = likesServices.findLike(id);
        ObjectId  blogID = likes.getBlogID();
        Blog blog = blogServices.findBlog(blogID);
        blog.getLikes().removeIf(x->x.getId().equals(id));
        likesServices.removeLikes(id);
        blogServices.saveBlog(blog);
        return true;
    }

    public Likes saveLike(Likes likes){
        likesServices.savelike(likes);
        return likes;
    }

    public List<Likes> findAllLikes(Blog blog){
        return blog.getLikes();
    }
}

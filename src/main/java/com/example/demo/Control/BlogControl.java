package com.example.demo.Control;

import com.example.demo.Entity.Blog;
import com.example.demo.Service.BlogServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
public class BlogControl {
    @Autowired
    BlogServices blogServices;

    public boolean addBlog(Blog blog){
        return blogServices.insertBlog(blog);
    }

    public Blog findBlog(ObjectId id){
        return blogServices.findBlog(id);
    }

    public boolean deleteBlog(ObjectId id){
        return blogServices.removeBlog(id);
    }

    public boolean editBlog(Blog blog){ return blogServices.saveBlog(blog);
    }

}

package com.example.demo.Control;

import com.example.demo.Entity.Blog;
import com.example.demo.Service.BlogServices;
import com.example.demo.Service.FilesServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
public class BlogControl {
    @Autowired
    BlogServices blogServices;
    FilesServices filesServices;

    public boolean addBlog(Blog blog){
        return blogServices.insertBlog(blog);
    }

    public Blog findBlog(ObjectId id){
        return blogServices.findBlog(id);
    }

    public boolean deleteBlog(ObjectId id){
        Blog blog = blogServices.findBlog(id);
        if(blog==null)
            return false ;
        return blogServices.removeBlog(id);
    }

    public boolean saveBlog(Blog blog){ return blogServices.saveBlog(blog);
    }

}

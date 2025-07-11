package com.example.demo.Control;

import com.example.demo.Entity.Blog;
import com.example.demo.Entity.User;
import com.example.demo.Operations.Control.CommentsControl;
import com.example.demo.Operations.Control.LikesControl;
import com.example.demo.Operations.Service.CommentServices;
import com.example.demo.Operations.Entity.Comments;
import com.example.demo.Operations.Entity.Likes;
import com.example.demo.Operations.Service.LikesServices;
import com.example.demo.Repository.BlogRepository;
import com.example.demo.Service.UserServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController

@RequestMapping("/user")
public class UserControl {
    @Autowired
    UserServices userServices;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    BlogControl blogControl;
    @Autowired
    BlogRepository blogRepository;
    @Autowired
    LikesControl likesControl;
    @Autowired
    CommentsControl commentsControl;
    @GetMapping
    public ResponseEntity<?> findUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userServices.findUser(username);
        if(user==null)
            return new ResponseEntity<>("User not found",HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }
    @DeleteMapping
    @Transactional
    public ResponseEntity<?> deleteUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userServices.findUser(username);
        for(Blog blog : user.getList()){
            blogControl.deleteBlog(blog.getId());
        }
        if(userServices.removeUser(username))
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>("User not found",HttpStatus.NO_CONTENT);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> editUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user1 = userServices.findUser(username);
        if(user1==null)
            return new ResponseEntity<>("User not found",HttpStatus.NO_CONTENT);
        user1.setEmail(user.getEmail());
        user1.setUsername(user.getUsername());
        user1.setPassword(passwordEncoder.encode(user.getPassword()));
        userServices.saveUser(user1);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/blogs")
    public ResponseEntity<List<Blog>> getBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Blog> blogPage = blogRepository.findAll(pageable);

        return new ResponseEntity<>(blogPage.getContent(), HttpStatus.OK);
    }
    @PostMapping("/new-blog")
    @Transactional
    public ResponseEntity<?> addBlog(@RequestBody Blog blog){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userServices.findUser(username);
        if(user==null)
            return new ResponseEntity<>("User not found",HttpStatus.NO_CONTENT);
        blog.setAuthor(username);
        blogControl.addBlog(blog);
        user.getList().add(blog);
        userServices.saveUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/edit-blog/{id}")
    @Transactional
    public ResponseEntity<?> editBlog(@RequestBody Blog blog, @PathVariable ObjectId id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userServices.findUser(username);
        if(user==null)
            return new ResponseEntity<>("User not found",HttpStatus.NO_CONTENT);
        for (Blog blog1 : user.getList()){
            if(blog1.getId().equals(id)){
                blog1.setTitle(blog.getTitle());
                blog1.setContent(blog.getContent());
                blog1.setLocalDateTime(LocalDateTime.now());
                blogControl.editBlog(blog1);
                userServices.saveUser(user);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Blog Not found",HttpStatus.NO_CONTENT);

    }

    @DeleteMapping("/delete-blog/{id}")
    @Transactional
    public ResponseEntity<?> deleteBlog(@PathVariable ObjectId id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userServices.findUser(username);
        Blog blog = blogControl.findBlog(id);
        if(blog==null){
            return new ResponseEntity<>("Blog not found",HttpStatus.NO_CONTENT);
        }
        user.getList().removeIf(a->a.getId().equals(id));
        userServices.saveUser(user);
        blogControl.deleteBlog(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }



    @PostMapping("/add-comment/{id}")
    @Transactional
    public ResponseEntity<?> commentBlog(@PathVariable ObjectId id, @RequestBody Comments comments){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Blog blog = blogControl.findBlog(id);
        if(blog==null)
            return new ResponseEntity<>("Blog not found",HttpStatus.NO_CONTENT);
        comments.setAuthor(username);
        return commentsControl.addComment(comments,blog);
    }


    @PostMapping("/add-likes/{id}")
    @Transactional
    public ResponseEntity<?> likeBlog(@PathVariable ObjectId id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Blog blog =blogControl.findBlog(id);
        if(blog==null)
            return new ResponseEntity<>("Blog not found",HttpStatus.NO_CONTENT);
        Likes likes = new Likes();
        likes.setUser(username);
        likesControl.LikeBlog(blog,likes);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/edit-comment/cmnt-id={id}")
    @Transactional
    public ResponseEntity<?> editComment(@PathVariable ObjectId id,@RequestBody Comments comments){
        return commentsControl.updateComment(id,comments);
    }
    @DeleteMapping("delete-comment/cmnt-id={id}")
    @Transactional
    public ResponseEntity<?> deleteComment(@PathVariable ObjectId id){
       return commentsControl.deleteComment(id);
    }



    @PostMapping("like-comment/cmnt-id={id}")
    @Transactional
    public ResponseEntity<?> likeComment(@PathVariable ObjectId id){
        return commentsControl.likeComment(id);
    }

    @DeleteMapping("delete-cmntlike/cmnt-id={id}")
    @Transactional
    public ResponseEntity<?> deleteLikeFromComment(@PathVariable ObjectId id){
        return commentsControl.deleteLikeComment(id);
    }

    @DeleteMapping("delete-like/like-id={id}")
    @Transactional
    public ResponseEntity<?> deleteLike(@PathVariable ObjectId id){
        return likesControl.deleteLike(id);
    }




    @PostMapping("reply-comment/cmnt-id={id}")
    @Transactional
    public ResponseEntity<?> replyComment(@RequestBody Comments comments,@PathVariable ObjectId id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        comments.setAuthor(username);
        return commentsControl.addReply(id,comments);
    }

}

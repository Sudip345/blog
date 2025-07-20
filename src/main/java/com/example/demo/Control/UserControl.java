package com.example.demo.Control;

import com.example.demo.Config.JwtUtil;
import com.example.demo.Entity.Blog;
import com.example.demo.Entity.User;
import com.example.demo.Operations.Control.CommentsControl;
import com.example.demo.Operations.Control.LikesControl;
import com.example.demo.Operations.Entity.Comments;
import com.example.demo.Operations.Entity.Likes;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@RestController

@RequestMapping("/user")
public class UserControl {
    @Autowired
    UserServices userServices;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    BlogControl blogControl;
    @Autowired
    BlogRepository blogRepository;
    @Autowired
    LikesControl likesControl;
    @Autowired
    CommentsControl commentsControl;
    @GetMapping
    public ResponseEntity<?> findUser(@CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    String username = jwtUtil.extractUsername(token);
        User user = userServices.findUser(username);
        if(user==null)
            return new ResponseEntity<>("User not found",HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(user,HttpStatus.OK);
    }


    @DeleteMapping
    @Transactional
    public ResponseEntity<?> deleteUser(@CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
     }

    String username = jwtUtil.extractUsername(token);
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
    public ResponseEntity<?> editUser(@RequestBody User user,@CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    String username = jwtUtil.extractUsername(token);
        User user1 = userServices.findUser(username);
        if(user1==null)
            return new ResponseEntity<>("User not found",HttpStatus.NO_CONTENT);
        if(userServices.findByUsernameOrEmail(!user.getUsername().isEmpty()?user.getUsername():"",!user.getEmail().isEmpty()?user.getEmail():"")!=null)
            return new ResponseEntity<>("User with this email or username already exists",HttpStatus.CONFLICT);
        user1.setEmail(!user.getEmail().isEmpty()?user.getEmail():user1.getEmail());
        user1.setUsername(!user.getUsername().isEmpty()?user.getUsername():user1.getUsername());
        user1.setPassword(!user.getPassword().isEmpty()?passwordEncoder.encode(user.getPassword()):user1.getPassword());
        userServices.saveUser(user1);

        return new ResponseEntity<>(user1,HttpStatus.OK);
    }

    @GetMapping("/blogs")
    public ResponseEntity<List<Blog>> getBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @CookieValue(name = "token", required = false) String token) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Blog> blogPage = blogRepository.findAll(pageable);

        return new ResponseEntity<>(blogPage.getContent(), HttpStatus.OK);
    }
    @PostMapping("/new-blog")
    @Transactional
    public ResponseEntity<?> addBlog(@RequestBody Blog blog,@CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    String username = jwtUtil.extractUsername(token);        User user = userServices.findUser(username);
        if(user==null)
            return new ResponseEntity<>("User not found",HttpStatus.NO_CONTENT);
        blog.setAuthor(username);
        blogControl.addBlog(blog);
        blog.setStringID(blog.getId()+"");
        blogControl.saveBlog(blog);
        user.getList().add(blog);
        userServices.saveUser(user);

        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @PutMapping("/edit-blog/{id}")
    @Transactional
    public ResponseEntity<?> editBlog(@RequestBody Blog blog, @PathVariable ObjectId id,
                                      @CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    String username = jwtUtil.extractUsername(token);
        User user = userServices.findUser(username);
        if(user==null)
            return new ResponseEntity<>("User not found",HttpStatus.NO_CONTENT);
        for (Blog blog1 : user.getList()){
            if(blog1.getId().equals(id)){
                blog1.setCaption(blog.getCaption());
                blog1.setLocalDateTime(LocalDateTime.now());
                blogControl.saveBlog(blog1);
                userServices.saveUser(user);
                return new ResponseEntity<>(blog1,HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Blog Not found",HttpStatus.NO_CONTENT);

    }

    @DeleteMapping("/delete-blog/{id}")
    @Transactional
    public ResponseEntity<?> deleteBlog(@PathVariable ObjectId id,@CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    String username = jwtUtil.extractUsername(token);        User user = userServices.findUser(username);
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
    public ResponseEntity<?> commentBlog(@PathVariable ObjectId id, @RequestBody Comments comments,
                                         @CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    String username = jwtUtil.extractUsername(token);        Blog blog = blogControl.findBlog(id);
        if(blog==null)
            return new ResponseEntity<>("Blog not found",HttpStatus.NO_CONTENT);
        comments.setAuthor(username);

        commentsControl.addComment(comments,blog);
        comments.setStringID(comments.getCommentId()+"");
        commentsControl.saveComment(comments);
        return new ResponseEntity<>(comments,HttpStatus.OK);
    }


    @PostMapping("/add-likes/{id}")
    @Transactional
    public ResponseEntity<?> likeBlog(@PathVariable ObjectId id,
                                      @CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    String username = jwtUtil.extractUsername(token);        Blog blog =blogControl.findBlog(id);
        if(blog==null)
            return new ResponseEntity<>("Blog not found",HttpStatus.NO_CONTENT);
        Likes likes = new Likes();
        likes.setUser(username);
        likesControl.LikeBlog(blog,likes);
        likes.setStringID(likes.getId()+"");
        likesControl.saveLike(likes);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/edit-comment/cmnt-id={id}")
    @Transactional
    public ResponseEntity<?> editComment(@PathVariable ObjectId id,@RequestBody Comments comments,
                                         @CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        Comments comments1= commentsControl.updateComment(id,comments);
        if(comments1==null)
            return new ResponseEntity<>("Comment not found",HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(comments1,HttpStatus.OK);
    }
    @DeleteMapping("delete-comment/cmnt-id={id}")
    @Transactional
    public ResponseEntity<?> deleteComment(@PathVariable ObjectId id,
                                           @CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
       boolean falg= commentsControl.deleteComment(id);
       if(falg)
           return new ResponseEntity<>(HttpStatus.OK);
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @PostMapping("like-comment/cmnt-id={id}")
    @Transactional
    public ResponseEntity<?> likeComment(@PathVariable ObjectId id,
                                         @CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        Comments comments= commentsControl.likeComment(id);
        if (comments==null)
            return new ResponseEntity<>("Comment not found",HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(comments,HttpStatus.OK);
    }

    @DeleteMapping("delete-cmntlike/cmnt-id={id}")
    @Transactional
    public ResponseEntity<?> deleteLikeFromComment(@PathVariable ObjectId id,
                                                   @CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        boolean flag= commentsControl.deleteLikeComment(id);
        if(flag) return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>("Comment not found",HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("delete-like/like-id={id}")
    @Transactional
    public ResponseEntity<?> deleteLike(@PathVariable ObjectId id,
                                        @CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
         likesControl.deleteLike(id);
         return new ResponseEntity<>(HttpStatus.OK);
    }




    @PostMapping("reply-comment/cmnt-id={id}")
    @Transactional
    public ResponseEntity<?> replyComment(@RequestBody Comments comments,@PathVariable ObjectId id,
                                          @CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }


    String username = jwtUtil.extractUsername(token);
        comments.setAuthor(username);
        Comments temp = commentsControl.addReply(id,comments);
        comments.setStringID(comments.getCommentId()+"");
        commentsControl.saveComment(comments);
        if(temp ==null)
            return new ResponseEntity<>("Comment not found",HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(comments,HttpStatus.OK);
    }


    @PostMapping("follow-user/user-id={UId}")
    @Transactional
    public ResponseEntity<?> followUser(@PathVariable ObjectId UId,
                                        @CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    String username = jwtUtil.extractUsername(token);
        User user1 = userServices.findUser(username);
        User user2 = userServices.findByID(UId);

        if(user2==null)
            return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
        if(user1.getFollowing().contains(user2.getUsername()))
            return new ResponseEntity<>("Already following "+user2.getUsername(),HttpStatus.CONFLICT);

        user1.getFollowing().add(user2.getUsername());
        user2.getFollowers().add(user1.getUsername());

        userServices.saveUser(user1);
        userServices.saveUser(user2);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @DeleteMapping("unfollow-user/user-id={UId}")
    @Transactional
    public ResponseEntity<?> unfollowUser(@PathVariable ObjectId UId,
                                          @CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    String username = jwtUtil.extractUsername(token);
        User user1 = userServices.findUser(username);
        User user2 = userServices.findByID(UId);

        if(user2==null || !user1.getFollowing().contains(user2.getUsername()))
            return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);

        user1.getFollowing().remove(user2.getUsername());
        user2.getFollowers().remove(user1.getUsername());
        userServices.saveUser(user1);
        userServices.saveUser(user2);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("remove-follower/user-id={UId}")
    @Transactional

    public ResponseEntity<?> removeFollowing(@PathVariable ObjectId UId,
                                             @CookieValue(name = "token", required = false) String token){
        if (token == null || !jwtUtil.validateToken(token)) {
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }

    String username = jwtUtil.extractUsername(token);
        User user1 = userServices.findUser(username);
        User user2 = userServices.findByID(UId);

        if(user2==null || !user1.getFollowers().contains(user2.getUsername()))
            return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);

        user1.getFollowers().remove(user2.getUsername());
        user2.getFollowing().remove(user1.getUsername());
        userServices.saveUser(user1);
        userServices.saveUser(user2);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

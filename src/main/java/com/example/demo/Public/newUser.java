package com.example.demo.Public;

import com.example.demo.Config.JwtUtil;
import com.example.demo.Entity.Blog;
import com.example.demo.Entity.User;
import com.example.demo.Repository.BlogRepository;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Component
@RequestMapping("/public")
public class newUser {
    @Autowired
    UserRepository userRepository;
    @Autowired
    BlogRepository blogRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Transactional
    @GetMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestBody User user){
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(user.getEmail()));
        User check = mongoTemplate.findOne(query,User.class);
        if(check==null)
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>("User exists with this email",HttpStatus.CONFLICT);
    }

    @Transactional
    @PostMapping("/new-user")
    public ResponseEntity<?> newUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).orElse(null) == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.getRoles().add("USER");
            userRepository.insert(user);

            String token = jwtUtil.generateToken(user.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", user.getUsername());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
    }


    @GetMapping("/blogs")
    public ResponseEntity<List<Blog>> getBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Blog> blogPage = blogRepository.findAll(pageable);

        return new ResponseEntity<>(blogPage.getContent(), HttpStatus.OK);
    }


}

package com.example.demo.Service;

import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UserServices {
    @Autowired
    UserRepository userRepository;

    public User findUser( String name){
        return userRepository.findByUsername(name).orElse(null);
    }

    public User findByID(ObjectId id){
        return userRepository.findById(id).orElse(null);
    }

    public boolean removeUser(String name){
        User user= userRepository.findByUsername(name).orElse(null);
        if(user==null)
            return false;

        userRepository.deleteByUsername(name);
        return true;
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public User findByUsernameOrEmail(String username , String email){
        return userRepository.findByUsernameOrEmail(username,email).orElse(null);
    }


}

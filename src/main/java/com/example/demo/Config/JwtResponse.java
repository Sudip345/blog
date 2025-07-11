package com.example.demo.Config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor

public class JwtResponse {
    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }
}

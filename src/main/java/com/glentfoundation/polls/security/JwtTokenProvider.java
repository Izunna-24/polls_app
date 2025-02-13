package com.glentfoundation.polls.security;


import com.glentfoundation.polls.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwtSecret}")
    private final String jwtSecret;

    @Value("${app.jwtExpirationInMinutes}")
    private final int jwtExpirationInMinutes;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMinutes);

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
    }

}

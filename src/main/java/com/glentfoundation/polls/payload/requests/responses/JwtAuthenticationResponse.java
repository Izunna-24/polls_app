package com.glentfoundation.polls.payload.requests.responses;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
}

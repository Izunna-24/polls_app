package com.glentfoundation.polls.payload.requests.responses;


import lombok.*;


@RequiredArgsConstructor
@Getter
public class JwtAuthenticationResponse {
    private final String accessToken;
    private final String tokenType = "Bearer";
}

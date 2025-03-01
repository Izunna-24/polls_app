package com.glentfoundation.polls.payload.requests.responses;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiResponse {
    private Boolean success;
    private String message;
}

package com.glentfoundation.polls.payload.requests.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserSummary {
    private Long id;
    private Long username;
    private Long name;
}

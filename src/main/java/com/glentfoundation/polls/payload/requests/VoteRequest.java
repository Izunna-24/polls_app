package com.glentfoundation.polls.payload.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteRequest {

    @NotNull
    private Long choiceId;
}

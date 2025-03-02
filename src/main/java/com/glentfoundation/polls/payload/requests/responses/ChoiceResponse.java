package com.glentfoundation.polls.payload.requests.responses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChoiceResponse {
    private long id;
    private String text;
    private Long voteCount;

}

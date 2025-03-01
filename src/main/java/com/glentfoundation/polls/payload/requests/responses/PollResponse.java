package com.glentfoundation.polls.payload.requests.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PollResponse {
    private Long id;
    private String question;
    private List<ChoioceResponse> choices;
    private UserSummary createdBy;
    private Instant creationDateTime;
    private Instant expirationDateTime;
    private Boolean isExpired;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long selectedChoice;
    private Long totalVotes;

}

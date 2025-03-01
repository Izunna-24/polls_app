package com.glentfoundation.polls.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ChoiceVoteCount {
    private Long choiceId;
    private Long voteCount;

}

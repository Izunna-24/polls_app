package com.glentfoundation.polls.services;

import com.glentfoundation.polls.models.Poll;
import com.glentfoundation.polls.payload.requests.PollRequest;
import com.glentfoundation.polls.payload.requests.VoteRequest;
import com.glentfoundation.polls.payload.requests.responses.PagedResponse;
import com.glentfoundation.polls.payload.requests.responses.PollResponse;
import com.glentfoundation.polls.security.UserPrincipal;

public interface PollService {

    PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int page, int size);
    PagedResponse<PollResponse> getPollsCreatedBy(String username, UserPrincipal currentUser, int page, int size);
    PagedResponse<PollResponse> getPollsVotedBy(String username, UserPrincipal currentUser, int page, int size);
    Poll createPoll(PollRequest pollRequest);
    PollResponse getPollById(Long pollId, UserPrincipal currentUser);
    PollResponse castVoteAndGetUpdatedPoll(Long pollId, VoteRequest voteRequest, UserPrincipal currentUser);
}


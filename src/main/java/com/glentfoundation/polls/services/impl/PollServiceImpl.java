package com.glentfoundation.polls.services.impl;

import com.glentfoundation.polls.models.Poll;
import com.glentfoundation.polls.payload.requests.PollRequest;
import com.glentfoundation.polls.payload.requests.VoteRequest;
import com.glentfoundation.polls.payload.requests.responses.PagedResponse;
import com.glentfoundation.polls.payload.requests.responses.PollResponse;
import com.glentfoundation.polls.security.UserPrincipal;
import com.glentfoundation.polls.services.PollService;
import org.springframework.stereotype.Service;

@Service
public class PollServiceImpl implements PollService {
    @Override
    public PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int page, int size) {
        return null;
    }

    @Override
    public PagedResponse<PollResponse> getPollsCreatedBy(String username, UserPrincipal currentUser, int page, int size) {
        return null;
    }

    @Override
    public PagedResponse<PollResponse> getPollsVotedBy(String username, UserPrincipal currentUser, int page, int size) {
        return null;
    }

    @Override
    public Poll createPoll(PollRequest pollRequest) {
        return null;
    }

    @Override
    public PollResponse getPollById(Long pollId, UserPrincipal currentUser) {
        return null;
    }

    @Override
    public PollResponse castVoteAndGetUpdatedPoll(Long pollId, VoteRequest voteRequest, UserPrincipal currentUser) {
        return null;
    }
}

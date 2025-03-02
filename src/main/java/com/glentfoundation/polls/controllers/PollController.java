package com.glentfoundation.polls.controllers;

import com.glentfoundation.polls.models.Poll;
import com.glentfoundation.polls.payload.requests.PollRequest;
import com.glentfoundation.polls.payload.requests.VoteRequest;
import com.glentfoundation.polls.payload.requests.responses.ApiResponse;
import com.glentfoundation.polls.payload.requests.responses.PagedResponse;
import com.glentfoundation.polls.payload.requests.responses.PollResponse;
import com.glentfoundation.polls.repository.PollRepository;
import com.glentfoundation.polls.repository.UserRepository;
import com.glentfoundation.polls.repository.VoteRepository;
import com.glentfoundation.polls.security.CurrentUser;
import com.glentfoundation.polls.security.UserPrincipal;
import com.glentfoundation.polls.utils.AppConstants;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/polls")
public class PollController {
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private PollService pollService;


    private static final  Logger logger = LoggerFactory.getLogger(PollController.class);

    @GetMapping
    public PagedResponse<PollResponse> getPolls(@CurrentUser UserPrincipal currentUser,
                                                @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return pollService.getAllPolls(currentUser,page,size);
    }


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPoll(@Valid @RequestBody PollRequest pollRequest) {
        Poll poll = pollService.createPoll(pollRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{pollId}")
                .buildAndExpand(poll.getId()).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true, "Poll created successfully " + poll));
    }

    @GetMapping("/{pollId}")
    public PollResponse getPollById(@CurrentUser UserPrincipal currentUser,
                                    @PathVariable Long pollId) {
        return pollService.getPollById(pollId, currentUser);
    }

    @PostMapping("/{pollId}/votes")
    @PreAuthorize("hasRole('USER')")
    public PollResponse castVote(@CurrentUser UserPrincipal currentUser,
                                 @PathVariable Long pollId,
                                 @Valid @RequestBody VoteRequest voteRequest) {
        return pollService.castVoteAndGetUpdatedPoll(pollId,voteRequest,currentUser);
    }
}

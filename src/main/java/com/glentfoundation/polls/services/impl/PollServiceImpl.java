package com.glentfoundation.polls.services.impl;

import com.glentfoundation.polls.exceptions.BadRequestException;
import com.glentfoundation.polls.exceptions.ResourceNotFoundException;
import com.glentfoundation.polls.models.*;
import com.glentfoundation.polls.models.Vote;
import com.glentfoundation.polls.payload.requests.PollRequest;
import com.glentfoundation.polls.payload.requests.VoteRequest;
import com.glentfoundation.polls.payload.requests.responses.PagedResponse;
import com.glentfoundation.polls.payload.requests.responses.PollResponse;
import com.glentfoundation.polls.repository.PollRepository;
import com.glentfoundation.polls.repository.UserRepository;
import com.glentfoundation.polls.repository.VoteRepository;
import com.glentfoundation.polls.security.UserPrincipal;
import com.glentfoundation.polls.services.PollService;
import com.glentfoundation.polls.utils.PollModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PollModelMapper pollModelMapper;

    private static final Logger logger = LoggerFactory.getLogger(PollServiceImpl.class);

    public PollServiceImpl(PollRepository pollRepository, VoteRepository voteRepository,
                           UserRepository userRepository, PollModelMapper pollModelMapper) {
        this.pollRepository = pollRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.pollModelMapper = pollModelMapper;
    }

    @Override
    public PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Poll> polls = pollRepository.findAll(pageable);
        return mapPollsToPagedResponse(polls, currentUser);
    }

    @Override
    public PagedResponse<PollResponse> getPollsCreatedBy(String username, UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Poll> polls = pollRepository.findAllByCreatedBy(user.getId(), pageable);
        return mapPollsToPagedResponse(polls, currentUser);
    }

    @Override
    public PagedResponse<PollResponse> getPollsVotedBy(String username, UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Long> userVotedPollIds = voteRepository.findVotedPollIdsByUserId(user.getId(), pageable);

        if (userVotedPollIds.isEmpty()) {
            return new PagedResponse<>(Collections.emptyList(), 0, size, 0, 0, true);
        }

        List<Long> pollIds = userVotedPollIds.getContent();
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Poll> polls = pollRepository.findAllByIdIn(pollIds, sort);

        return mapPollsToPagedResponse(new PageImpl<>(polls), currentUser);
    }

    @Override
    public Poll createPoll(PollRequest pollRequest) {
        Poll poll = new Poll();
        poll.setQuestion(pollRequest.getQuestion());

        pollRequest.getChoices().forEach(choiceRequest -> poll.addChoice(new Choice(choiceRequest.getText())));

        poll.setExpirationDateTime(Instant.now()
                .plus(Duration.ofDays(pollRequest.getPollLength().getDays()))
                .plus(Duration.ofHours(pollRequest.getPollLength().getHours())));

        return pollRepository.save(poll);
    }

    @Override
    public PollResponse getPollById(Long pollId, UserPrincipal currentUser) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll", "id", pollId));
        return generatePollResponse(poll, currentUser);
    }

    @Transactional
    @Override
    public PollResponse castVoteAndGetUpdatedPoll(Long pollId, VoteRequest voteRequest, UserPrincipal currentUser) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll", "id", pollId));
        if (poll.getExpirationDateTime().isBefore(Instant.now())) {
            throw new BadRequestException("Sorry! This Poll has already expired");
        }
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        Choice selectedChoice = poll.getChoices().stream()
                .filter(choice -> choice.getId().equals(voteRequest.getChoiceId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Choice", "id", voteRequest.getChoiceId()));

        Vote vote = new Vote(user, poll, selectedChoice);
        try {
            voteRepository.save(vote);
        } catch (DataIntegrityViolationException exception) {
            logger.info("User {} has already voted in Poll {}", currentUser.getId(), pollId);
            throw new BadRequestException("Sorry! You already casted your vote in this poll");
        }
        return generatePollResponse(poll, currentUser);
    }


    private PagedResponse<PollResponse> mapPollsToPagedResponse(Page<Poll> polls, UserPrincipal currentUser) {
        if (polls.isEmpty()) {
            return new PagedResponse<>(Collections.emptyList(), 0, polls.getSize(), 0, 0, true);
        }

        List<Long> pollIds = polls.map(Poll::getId).getContent();
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);
        Map<Long, User> creatorMap = getPollCreatorMap(polls.getContent());

        List<PollResponse> pollResponses = polls.map(poll ->
                pollModelMapper.mapPollToPollResponse(poll, choiceVoteCountMap, creatorMap.get(poll.getCreatedBy()),
                        pollUserVoteMap == null ? null : pollUserVoteMap.getOrDefault(poll.getId(), null))
        ).getContent();

        return new PagedResponse<>(pollResponses, polls.getNumber(), polls.getSize(),
                polls.getTotalElements(), polls.getTotalPages(), polls.isLast());
    }

    private PollResponse generatePollResponse(Poll poll, UserPrincipal currentUser) {
        List<ChoiceVoteCount> votes = voteRepository.countByPollIdGroupByChoiceId(poll.getId());
        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));

        User creator = userRepository.findById(poll.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", poll.getCreatedBy()));

        Vote userVote = (currentUser != null)
                ? voteRepository.findByUserIdAndPollId(currentUser.getId(), poll.getId())
                : null;

        return pollModelMapper.mapPollToPollResponse(poll, choiceVotesMap, creator,
                userVote != null ? userVote.getChoice().getId() : null);
    }

    private Map<Long, Long> getChoiceVoteCountMap(List<Long> pollIds) {
        List<ChoiceVoteCount> votes = voteRepository.countByPollIdInGroupByChoiceId(pollIds);
        return votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));
    }

    private Map<Long, Long> getPollUserVoteMap(UserPrincipal currentUser, List<Long> pollIds) {
        if (currentUser == null) {
            return Collections.emptyMap();
        }
        List<Vote> userVotes = voteRepository.findByUserIdAndPollIdIn(currentUser.getId(), pollIds);
        return userVotes.stream()
                .collect(Collectors.toMap(vote -> vote.getPoll().getId(), vote -> vote.getChoice().getId()));
    }

    private Map<Long, User> getPollCreatorMap(List<Poll> polls) {
        List<Long> creatorIds = polls.stream()
                .map(Poll::getCreatedBy)
                .distinct()
                .collect(Collectors.toList());

        List<User> creators = userRepository.findByIdIn(creatorIds);
        return creators.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) throw new BadRequestException("Page number cannot be less than zero.");
        if (size > 50) throw new BadRequestException("Page size must not be greater than 50.");
    }
}


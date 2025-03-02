package com.glentfoundation.polls.utils;

import com.glentfoundation.polls.models.Poll;
import com.glentfoundation.polls.models.User;
import com.glentfoundation.polls.payload.requests.responses.ChoiceResponse;
import com.glentfoundation.polls.payload.requests.responses.PollResponse;
import com.glentfoundation.polls.payload.requests.responses.UserSummary;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PollModelMapper {

    private final ModelMapper modelMapper;

    public PollModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PollResponse mapPollToPollResponse(Poll poll, Map<Long, Long> choiceVotesMap, User creator, Long userVote) {
        PollResponse pollResponse = modelMapper.map(poll, PollResponse.class);
        pollResponse.setIsExpired(poll.getExpirationDateTime().isBefore(Instant.now()));

        List<ChoiceResponse> choiceResponses = poll.getChoices()
                .stream()
                .map(choice -> {
                    ChoiceResponse choiceResponse = modelMapper.map(choice, ChoiceResponse.class);
                    choiceResponse.setVoteCount(choiceVotesMap.getOrDefault(choice.getId(), 0L));
                    return choiceResponse;
                })
                .collect(Collectors.toList());

        pollResponse.setChoices(choiceResponses);
        pollResponse.setCreatedBy(new UserSummary(creator.getId(), creator.getUsername(), creator.getName()));

        if (userVote != null) {
            pollResponse.setSelectedChoice(userVote);
        }

        pollResponse.setTotalVotes(choiceResponses.stream().mapToLong(ChoiceResponse::getVoteCount).sum());

        return pollResponse;
    }
}

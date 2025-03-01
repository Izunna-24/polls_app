package com.glentfoundation.polls.repository;

import com.glentfoundation.polls.models.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    Optional<Poll> findById(Long pollId);
    Page<Poll> findAllByCreatedBy(Long userId, Pageable pageable);
    long countByCreatedBy(Long userId);
    List<Poll> findAllByIdIn(List<Long> pollIds);

    List<Poll> findAllByIdIn(List<Long> pollIds, Sort sort);

}

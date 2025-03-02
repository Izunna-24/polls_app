package com.glentfoundation.polls.models;

import com.glentfoundation.polls.models.audit.DateAudit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "poll_id",
                "user_id"
        })
})


@Getter
@Setter
@NoArgsConstructor
public class Vote extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "choice_id", nullable = false)
    private Choice choice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Vote(User user, Poll poll, Choice choice) {
        this.user = user;
        this.poll = poll;
        this.choice = choice;
    }



}

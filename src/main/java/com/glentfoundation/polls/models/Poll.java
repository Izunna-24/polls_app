package com.glentfoundation.polls.models;


import com.glentfoundation.polls.models.audit.UserDateAudit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "polls")
@Getter
@Setter
public class Poll extends UserDateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String question;

    @OneToMany(
            mappedBy = "poll",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Size(min = 2, max = 6)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 30)
    private List<Choice> choices = new ArrayList<>();

    @NotNull
    private Instant expirationDateTime;

    public void addChoice(Choice choice) {
        choices.add(choice);
        choice.setPoll(this);
    }
    public void removeChoice(Choice choice) {
        choices.remove(choice);
        choice.setPoll(null);
    }
}

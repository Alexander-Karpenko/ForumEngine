package com.AtomIDTest.ForumEngine.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "topic")
public class Topic {
    @Id
    @Column(name = "id")
    private UUID id;

    @NotEmpty(message = "Topic name shouldn't be empty")
    @Column(name = "name")
    private String name;

    @Column(name = "created")
    private LocalDateTime created;

    @JsonIgnore
    @Column(name = "author")
    private String author;

    @NotNull(message = "topic should have at least one message")
    @OneToMany(mappedBy = "topic")
    private List<Message> messages;

    public Topic(String name, LocalDateTime created, String author, List<Message> messages) {
        this.name = name;
        this.created = created;
        this.author = author;
        this.messages = messages;
    }

}

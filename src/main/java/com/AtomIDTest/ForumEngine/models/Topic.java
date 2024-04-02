package com.AtomIDTest.ForumEngine.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table
public class Topic {
    @Id
    @Column(name = "id")
    private UUID id;

    @NotEmpty(message = "Topic name shouldn't be empty")
    @Column(name = "name")
    private String name;

    @Column(name = "created")
    private LocalDateTime created;

    @NotNull(message = "topic should have at least one message")
    @OneToMany(mappedBy = "topic")
    private List<Message> messages;

    public Topic() {
    }

    public Topic( String name, LocalDateTime created, List<Message> messages) {
        this.name = name;
        this.created = created;
        this.messages = messages;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}

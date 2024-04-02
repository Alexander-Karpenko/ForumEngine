package com.AtomIDTest.ForumEngine.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @NotNull(message = "Message id shouldn`t be empty")
    @Column(name = "id")
    private UUID id ;

    @NotBlank(message = "Message text shouldn't be empty")
    @Column(name = "text")
    private String text;

    @Column(name = "author")
    private String author;

    @Column(name = "created")
    private LocalDateTime created;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    public Message() {
    }

    public Message( String text, String author, LocalDateTime created, Topic topic) {
        this.text = text;
        this.author = author;
        this.created = created;
        this.topic = topic;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID uuid) {
        this.id = uuid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", author='" + author + '\'' +
                ", created=" + created +
                ", topic=" + topic +
                '}';
    }
}

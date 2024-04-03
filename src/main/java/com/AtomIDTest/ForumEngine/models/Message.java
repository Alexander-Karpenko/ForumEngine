package com.AtomIDTest.ForumEngine.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@Entity
@NoArgsConstructor
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

    public Message( String text, String author, LocalDateTime created, Topic topic) {
        this.text = text;
        this.author = author;
        this.created = created;
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

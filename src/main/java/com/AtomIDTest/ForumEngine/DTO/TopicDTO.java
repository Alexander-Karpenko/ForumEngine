package com.AtomIDTest.ForumEngine.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.UUID;

public class TopicDTO {

    @NotNull(message = "Topic id shouldn't be empty")
    private UUID id;

    @NotEmpty(message = "Topic name shouldn't be empty")
    private String name;

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

    @Override
    public String toString() {
        return "TopicDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

package com.AtomIDTest.ForumEngine.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class MessageDTO {
    @NotNull(message = "message id shouldn`t be empty")
    private UUID id;

    @NotBlank(message = "Message text shouldn't be empty")
    private String text;

//    private String author; //TODO после заменить так, чтобы имя бралось у пользователя

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}

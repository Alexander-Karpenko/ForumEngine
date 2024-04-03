package com.AtomIDTest.ForumEngine.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MessageDTO {
    @NotNull(message = "message id shouldn`t be empty")
    private UUID id;

    @NotBlank(message = "Message text shouldn't be empty")
    private String text;

}

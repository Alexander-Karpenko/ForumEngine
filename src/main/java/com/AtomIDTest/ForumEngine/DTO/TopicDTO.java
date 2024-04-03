package com.AtomIDTest.ForumEngine.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.UUID;
@Data
public class TopicDTO {

    @NotNull(message = "Topic id shouldn't be empty")
    private UUID id;

    @NotEmpty(message = "Topic name shouldn't be empty")
    private String name;

}

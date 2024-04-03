package com.AtomIDTest.ForumEngine.DTO;

import com.AtomIDTest.ForumEngine.models.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class NewTopicDTO {

    @NotBlank(message = "topic name shouldn't be blank")
    @NotNull(message = "topic name shouldn't be empty")
    private String topicName;

    @NotNull(message = "topic should have at least one message")
    private List<Message> messages;

}

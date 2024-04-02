package com.AtomIDTest.ForumEngine.DTO;

import com.AtomIDTest.ForumEngine.models.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class NewTopicDTO {

    @NotBlank(message = "topic name shouldn't be blank")
    @NotNull(message = "topic name shouldn't be empty")
    private String topicName;

    @NotNull(message = "topic should have at least one message")
    private List<Message> messages;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "NewTopicDTO{" +
                "topicName='" + topicName + '\'' +
                ", messages=" + messages +
                '}';
    }
}

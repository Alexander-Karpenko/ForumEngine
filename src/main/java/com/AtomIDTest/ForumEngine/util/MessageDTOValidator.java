package com.AtomIDTest.ForumEngine.util;

import com.AtomIDTest.ForumEngine.DTO.MessageDTO;
import com.AtomIDTest.ForumEngine.services.MessagesService;
import com.AtomIDTest.ForumEngine.services.TopicsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MessageDTOValidator {
    private final UUIDValidator uuidValidator;
    private final TopicsService topicsService;
    private final MessagesService messagesService;


    @Autowired
    public MessageDTOValidator(UUIDValidator uuidValidator, TopicsService topicsService, MessagesService messagesService) {
        this.uuidValidator = uuidValidator;
        this.topicsService = topicsService;
        this.messagesService = messagesService;
    }

    public void validate(String topicId, MessageDTO messageDTO){
        if(uuidValidator.checkingForIncorrectUUID(topicId)){
            throw new InvalidTopicIdException("Invalid input - Invalid topic ID");
        }
        if(topicsService.findById(UUID.fromString(topicId)).isEmpty()){
            throw new TopicNotFoundException("Invalid input - Topic with this id not found");
        }
        if(messagesService.findById(messageDTO.getId()).isPresent()){
            throw new InvalidMessageIdException("Validation exception - Message with that id already exist");
        }
    }
}

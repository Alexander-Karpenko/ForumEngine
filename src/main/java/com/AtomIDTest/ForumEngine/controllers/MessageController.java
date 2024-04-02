package com.AtomIDTest.ForumEngine.controllers;

import com.AtomIDTest.ForumEngine.DTO.MessageDTO;
import com.AtomIDTest.ForumEngine.models.Message;
import com.AtomIDTest.ForumEngine.models.Topic;
import com.AtomIDTest.ForumEngine.services.MessagesService;
import com.AtomIDTest.ForumEngine.services.TopicsService;
import com.AtomIDTest.ForumEngine.util.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/topic/{topicId}/message", "/api/v1/message/{messageId}" })
public class MessageController {

    private final MessagesService messagesService ;
    private final TopicsService topicsService;
    private final UUIDValidator uuidValidator;
    private final MessageDTOValidator messageDTOValidator;

        @Autowired
    public MessageController(MessagesService messagesService, TopicsService topicsService, UUIDValidator uuidValidator, MessageDTOValidator messageDTOValidator) {
        this.messagesService = messagesService;
            this.topicsService = topicsService;
            this.uuidValidator = uuidValidator;
            this.messageDTOValidator = messageDTOValidator;
        }

    @PostMapping
    public ResponseEntity<Topic> createMessage(@PathVariable String topicId, @RequestBody @Valid MessageDTO messageDTO,
                                               BindingResult bindingResult){
        messageDTOValidator.validate(topicId, messageDTO);
        if (bindingResult.hasErrors()){
            ErrorsOut.returnErrors(bindingResult);
        }
        messagesService.save(messageDTO, UUID.fromString(topicId));
        return ResponseEntity.ok(topicsService.findById(UUID.fromString(topicId)).orElse(null));
    }

    @PutMapping
    public ResponseEntity<Topic> updateMessage(@PathVariable String topicId, @RequestBody @Valid MessageDTO messageDTO,
                                               BindingResult bindingResult){
        if(uuidValidator.checkingForIncorrectUUID(topicId)){
            throw new InvalidTopicIdException("Invalid topic ID");
        }
        if(topicsService.findById(UUID.fromString(topicId)).isEmpty()){
            throw new TopicNotFoundException("Topic not found");
        }

        if(bindingResult.hasErrors()){
            ErrorsOut.returnErrors(bindingResult);
        }
        Message message = messagesService.findById(messageDTO.getId())
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));
        message.setText(messageDTO.getText());

        messagesService.save(message);

        return ResponseEntity.ok(topicsService.findById(UUID.fromString(topicId)).orElse(null));
    }

    @DeleteMapping
    public ResponseEntity<HttpStatusCode> deleteMessage(@PathVariable String messageId){
            messagesService.delete(UUID.fromString(messageId));
        return ResponseEntity.ok(HttpStatusCode.valueOf(204));
    }



    @ExceptionHandler
    private ResponseEntity<String> handleException (InvalidTopicIdException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(400));
    }
    @ExceptionHandler
    private ResponseEntity<String> handleException (NotCreatedException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(400));
    }
    @ExceptionHandler
    private ResponseEntity<String> handleException (TopicNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(404));
    }
    @ExceptionHandler
    private ResponseEntity<String> handleException (HttpMessageNotReadableException e){
        return new ResponseEntity<>("Invalid input - Invalid message id", HttpStatusCode.valueOf(400));
    }
    @ExceptionHandler
    private ResponseEntity<String> handleException (InvalidMessageIdException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(420));
    }
    @ExceptionHandler
    private ResponseEntity<String> handleException (MessageNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(404));
    }
}

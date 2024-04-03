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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/topic/{topicId}/message", "/api/v1/message/{messageId}" })
public class MessageController {

    private final MessagesService messagesService ;
    private final TopicsService topicsService;
    private final UUIDValidator uuidValidator;
    private final MessageDTOValidator messageDTOValidator;
    private final MessagePaginator messagePaginator;

        @Autowired
    public MessageController(MessagesService messagesService, TopicsService topicsService, UUIDValidator uuidValidator, MessageDTOValidator messageDTOValidator, MessagePaginator messagePaginator) {
        this.messagesService = messagesService;
            this.topicsService = topicsService;
            this.uuidValidator = uuidValidator;
            this.messageDTOValidator = messageDTOValidator;
            this.messagePaginator = messagePaginator;
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
    @PostMapping(params = { "page", "message_per_page"})
    public ResponseEntity<Topic> createMessageWithPagination(@PathVariable String topicId, @RequestBody @Valid MessageDTO messageDTO,
                                               BindingResult bindingResult,
                                               @RequestParam(name = "page") int page,
                                               @RequestParam(name = "message_per_page") int size){
        messageDTOValidator.validate(topicId, messageDTO);
        if (bindingResult.hasErrors()){
            ErrorsOut.returnErrors(bindingResult);
        }
        messagesService.save(messageDTO, UUID.fromString(topicId));
        return ResponseEntity.ok(messagePaginator.paginate(Objects.requireNonNull(topicsService.findById(UUID.fromString(topicId))
                .orElse(null)), page, size));
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(!message.getAuthor().equals(userDetails.getUsername()) && auth != null &&
                auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            throw new AccessDeniedException("You cannot edit a message not created by you. ");
        }
        message.setText(messageDTO.getText());

        messagesService.save(message);

        return ResponseEntity.ok(topicsService.findById(UUID.fromString(topicId)).orElse(null));
    }
    @PutMapping(params = { "page", "message_per_page"})
    public ResponseEntity<Topic> updateMessageWithPagination(@PathVariable String topicId, @RequestBody @Valid MessageDTO messageDTO,
                                               BindingResult bindingResult,
                                               @RequestParam(name = "page") int page,
                                               @RequestParam(name = "message_per_page") int size){
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(!message.getAuthor().equals(userDetails.getUsername()) && auth != null &&
                auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            throw new AccessDeniedException("You cannot edit a message not created by you. ");
        }

        message.setText(messageDTO.getText());
        messagesService.save(message);

        return ResponseEntity.ok(messagePaginator.paginate(Objects.requireNonNull(topicsService.findById(UUID.fromString(topicId))
                .orElse(null)), page, size));
    }


    @DeleteMapping
    public ResponseEntity<HttpStatusCode> deleteMessage(@PathVariable String messageId){
        if(uuidValidator.checkingForIncorrectUUID(messageId)){
            throw new InvalidTopicIdException("Invalid message ID");
        }
        Message message = messagesService.findById(UUID.fromString(messageId))
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!message.getAuthor().equals(userDetails.getUsername()) && auth != null &&
                auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            throw new AccessDeniedException("You cannot delete a message not created by you. ");
        }
            messagesService.delete(UUID.fromString(messageId));
        return new ResponseEntity<>(HttpStatusCode.valueOf(204));
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
    @ExceptionHandler
    private ResponseEntity<String> handleException (InvalidPaginationException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(400));
    }
}

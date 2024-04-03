package com.AtomIDTest.ForumEngine.controllers;

import com.AtomIDTest.ForumEngine.DTO.NewTopicDTO;
import com.AtomIDTest.ForumEngine.DTO.TopicDTO;
import com.AtomIDTest.ForumEngine.models.Message;
import com.AtomIDTest.ForumEngine.models.Topic;
import com.AtomIDTest.ForumEngine.services.MessagesService;
import com.AtomIDTest.ForumEngine.services.TopicsService;
import com.AtomIDTest.ForumEngine.util.*;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/topic")
public class TopicController {
    private final TopicsService topicsService;
    private final ModelMapper modelMapper;
    private final UUIDValidator uuidValidator;
    private final MessagesService messagesService;
    private final NewTopicDTOMessageValidator newTopicDTOMessageValidator;
    private final MessagePaginator messagePaginator;

    @Autowired
    public TopicController(TopicsService topicsService, ModelMapper modelMapper, UUIDValidator uuidValidator, MessagesService messagesService, NewTopicDTOMessageValidator newTopicDTOMessageValidator, MessagePaginator messagePaginator) {
        this.topicsService = topicsService;
        this.modelMapper = modelMapper;
        this.uuidValidator = uuidValidator;
        this.messagesService = messagesService;
        this.newTopicDTOMessageValidator = newTopicDTOMessageValidator;
        this.messagePaginator = messagePaginator;
    }

    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody @Valid NewTopicDTO newTopicDTO,
                                             BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            ErrorsOut.returnErrors(bindingResult);
        }
        if(newTopicDTO.getMessages() != null) {
            newTopicDTOMessageValidator.validate(newTopicDTO.getMessages());
        }
        UUID topicUuid = UUID.randomUUID();
        messagesService.save(newTopicDTO.getMessages().get(0), topicUuid);
        topicsService.save(newTopicDTO, topicUuid);
        messagesService.save(newTopicDTO.getMessages().get(0), topicUuid);
        //  Есть огромное подозрение что это костыль, но без предварительного сохранения
        //  сообщения тема не создается, а сообщению нельзя сразу задать id темы, тк темы нет в бд.
        //  Я уверен, что есть решение получше, тк я лишний раз нагружаю бд, но я его еще не нашел

        Topic topic = topicsService.findById(topicUuid).orElse(null);
        return ResponseEntity.ok(topic);
    }

    @PutMapping
    public ResponseEntity<List<Topic>> updateTopic(@RequestBody @Valid TopicDTO topicDTO,
                                                   BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            ErrorsOut.returnErrors(bindingResult);
        }
        Topic topic = topicsService.findById(topicDTO.getId())
                .orElseThrow(() -> new TopicNotFoundException("Topic not found"));
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        if(!topic.getAuthor().equals(userDetails.getUsername()) && auth != null &&
                auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            throw new AccessDeniedException("You cannot edit a topic not created by you. ");
        }
        topic.setName(topicDTO.getName());
        topicsService.save(topic);
        return ResponseEntity.ok(topicsService.findAll());
    }

    @PutMapping(params = { "page", "topic_per_page" })
    public ResponseEntity<List<Topic>> updateTopicWithPagination(@RequestBody @Valid TopicDTO topicDTO,
                                                   BindingResult bindingResult, @RequestParam(name = "page") int page,
                                                   @RequestParam(name = "topic_per_page") int size){
        if(bindingResult.hasErrors()){
            ErrorsOut.returnErrors(bindingResult);
        }
        Topic topic = topicsService.findById(topicDTO.getId())
                .orElseThrow(() -> new TopicNotFoundException("Topic not found"));
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!topic.getAuthor().equals(userDetails.getUsername()) && auth != null &&
                auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            throw new AccessDeniedException("You cannot edit a topic not created by you. ");
        }
        topic.setName(topicDTO.getName());
        topicsService.save(topic);

        return ResponseEntity.ok(topicsService.findAll(page,size));
    }


    @GetMapping
    public ResponseEntity<List<TopicDTO>> listAllTopics(){
        return ResponseEntity.ok(topicsService.findAll().stream().map(this::convertToTopicDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping(params = { "page", "topic_per_page" })
    public ResponseEntity<List<TopicDTO>> listAllTopicsWithPagination(@RequestParam(name = "page") int page,
                                                        @RequestParam(name = "topic_per_page") int size){
        return ResponseEntity.ok(topicsService.findAll(page,size).stream().map(this::convertToTopicDTO)
                .collect(Collectors.toList()));
    }

    @RequestMapping("/{topicId}")
    @GetMapping
    public ResponseEntity<Optional<Topic>> listTopicMessages(@PathVariable String topicId) {
        if(uuidValidator.checkingForIncorrectUUID(topicId)){
            throw new InvalidTopicIdException("Invalid topic ID");
        }
        Optional<Topic> topic = topicsService.findById(UUID.fromString(topicId));
        return ResponseEntity.ok(topic);
    }
    @RequestMapping(path = "/{topicId}",params = { "page", "message_per_page"})
    @GetMapping
    public ResponseEntity<Topic> listTopicMessagesWithPagination(@PathVariable String topicId,
                                                             @RequestParam(name = "page") int page,
                                                             @RequestParam(name = "message_per_page") int size) {
        if(uuidValidator.checkingForIncorrectUUID(topicId)){
            throw new InvalidTopicIdException("Invalid topic ID");
        }
        Optional<Topic> topic = topicsService.findById(UUID.fromString(topicId));
        assert topic.orElse(null) != null;
        return ResponseEntity.ok(messagePaginator.paginate(topic.orElse(null), page, size));
    }



    public TopicDTO convertToTopicDTO(Topic topic){
        return modelMapper.map(topic, TopicDTO.class);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleException (HttpMessageNotReadableException e){
        return new ResponseEntity<>("Invalid ID supplied", HttpStatusCode.valueOf(400));
    }
    @ExceptionHandler
    private ResponseEntity<String> handleException (TopicNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(404));
    }
    @ExceptionHandler
    private ResponseEntity<String> handleException (NotCreatedException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(400));
    }
    @ExceptionHandler
    private ResponseEntity<String> handleException (BadRequestException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(400));
    }
    @ExceptionHandler
    private ResponseEntity<String> handleException (UnprocessableEntityException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(420));
    }
    @ExceptionHandler
    private ResponseEntity<String> handleException (InvalidTopicIdException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(400));
    }
    @ExceptionHandler
    private ResponseEntity<String> handleException (InvalidPaginationException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(400));
    }
    @ExceptionHandler
    private ResponseEntity<String> handleException (AccessDeniedException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(403));
    }

}

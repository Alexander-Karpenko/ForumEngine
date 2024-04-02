package com.AtomIDTest.ForumEngine.controllers;

import com.AtomIDTest.ForumEngine.DTO.NewTopicDTO;
import com.AtomIDTest.ForumEngine.DTO.TopicDTO;
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
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    public TopicController(TopicsService topicsService, ModelMapper modelMapper, UUIDValidator uuidValidator, MessagesService messagesService, NewTopicDTOMessageValidator newTopicDTOMessageValidator) {
        this.topicsService = topicsService;
        this.modelMapper = modelMapper;
        this.uuidValidator = uuidValidator;
        this.messagesService = messagesService;
        this.newTopicDTOMessageValidator = newTopicDTOMessageValidator;
    }

    @PostMapping
    public ResponseEntity<String> createTopic(@RequestBody @Valid NewTopicDTO newTopicDTO,
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

        return new ResponseEntity<>("Successful operation", HttpStatusCode.valueOf(200));
    }

    @PutMapping
    public ResponseEntity<Topic> updateTopic(@RequestBody @Valid TopicDTO topicDTO,
                                             BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            ErrorsOut.returnErrors(bindingResult);
        }
        Topic topic = topicsService.findById(topicDTO.getId())
                .orElseThrow(() -> new TopicNotFoundException("Topic not found"));
        topic.setName(topicDTO.getName());
        topicsService.save(topic);
        return ResponseEntity.ok(topic);
    }

    @GetMapping
    public ResponseEntity<List<TopicDTO>> listAllTopics(){
        return ResponseEntity.ok(topicsService.findAll().stream().map(this::convertToTopicDTO)
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

}

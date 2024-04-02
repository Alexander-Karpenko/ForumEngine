package com.AtomIDTest.ForumEngine.services;

import com.AtomIDTest.ForumEngine.DTO.MessageDTO;
import com.AtomIDTest.ForumEngine.models.Message;
import com.AtomIDTest.ForumEngine.repositories.MessagesRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessagesService {
    private final MessagesRepository messagesRepository;
    private final TopicsService topicsService;
    private final ModelMapper modelMapper;

        @Autowired
    public MessagesService(MessagesRepository messagesRepository, TopicsService topicsService, ModelMapper modelMapper)
    {
        this.messagesRepository = messagesRepository;
        this.topicsService = topicsService;
        this.modelMapper = modelMapper;
    }


    public void save(MessageDTO messageDTO, UUID topicId){
        messagesRepository.save(convertToMessage(messageDTO,topicId));
    }
    public void save(Message message, UUID topicId){
        enrichMessage(message,topicId);
        messagesRepository.save(message);
    }
    public void save(Message message){
        messagesRepository.save(message);
    }

    public Optional<Message> findById(UUID uuid){
            return messagesRepository.findById(uuid);
    }

    public void delete(UUID uuid){
            messagesRepository.deleteById(uuid);
    }

    private Message convertToMessage(MessageDTO messageDTO, UUID topicId){
        Message message = modelMapper.map(messageDTO, Message.class);
        enrichMessage(message,topicId);
        return message;
    }
    private void enrichMessage(Message message, UUID topicId){
        //TODO message.setAuthor(взять с JWT)
        message.setCreated(LocalDateTime.now());
        message.setTopic(topicsService.findById(topicId).orElse(null));
    }

}

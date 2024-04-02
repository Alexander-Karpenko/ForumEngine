package com.AtomIDTest.ForumEngine.services;

import com.AtomIDTest.ForumEngine.DTO.NewTopicDTO;
import com.AtomIDTest.ForumEngine.models.Topic;
import com.AtomIDTest.ForumEngine.repositories.TopicsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TopicsService {
    private final TopicsRepository topicsRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TopicsService(TopicsRepository topicsRepository, ModelMapper modelMapper) {
        this.topicsRepository = topicsRepository;
        this.modelMapper = modelMapper;
    }

    public void save(NewTopicDTO newTopicDTO, UUID topicUuid){
        Topic topic = convertToTopic(newTopicDTO);
        enrichTopic(topic, topicUuid);
        topicsRepository.save(topic);
    }

    public void save(Topic topic){
        topicsRepository.save(topic);
    }

    public List<Topic> findAll(){
        return topicsRepository.findAll();
    }
    public List<Topic> findAll(int pageNum, int size){
        return topicsRepository.findAll(PageRequest.of(pageNum, size)).getContent();
    }

    public Optional<Topic> findById(UUID id){
        return topicsRepository.findById(id);
    }
    public Optional<Topic> findByName(String name){
        return topicsRepository.findByName(name);
    }

    private Topic convertToTopic(NewTopicDTO newTopicDTO){
        Topic topic = modelMapper.map(newTopicDTO, Topic.class);
        return topic;
    }

    private void enrichTopic(Topic topic, UUID topicUuid){
        topic.setId(topicUuid);
        topic.setCreated(LocalDateTime.now());
    }
}

package com.AtomIDTest.ForumEngine.util;

import com.AtomIDTest.ForumEngine.DTO.NewTopicDTO;
import com.AtomIDTest.ForumEngine.models.Message;
import com.AtomIDTest.ForumEngine.services.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class NewTopicDTOMessageValidator {

    private final UUIDValidator uuidValidator ;
    private final MessagesService messagesService;

    @Autowired
    public NewTopicDTOMessageValidator(UUIDValidator uuidValidator, MessagesService messagesService) {
        this.uuidValidator = uuidValidator;
        this.messagesService = messagesService;
    }

    public void validate(List<Message> messages){
        StringBuilder errMessage = new StringBuilder();
        errMessage.append("Invalid input - ");

        if (messages.isEmpty()){
            errMessage.append("Topic must have at least one message. ");
            throw new BadRequestException(errMessage.toString());
        }

        if (messages.get(0).getId() == null){
            errMessage.append("Message id shouldn`t be empty. ");
            throw new BadRequestException(errMessage.toString());
        }

        if (messages.get(0).getText() == null){
            errMessage.append("Message text shouldn`t be empty. ");
            throw new BadRequestException(errMessage.toString());
        }

        errMessage.delete(0 , errMessage.length());
        errMessage.append("Validation exception - ");
        if(messages.size() > 1){
            errMessage.append("Created topic must have only one message. ");
            throw new UnprocessableEntityException(errMessage.toString());
        }
        if(messagesService.findById(messages.get(0).getId()).isPresent()){
            errMessage.append("Message with this id already exist. ");
            throw new UnprocessableEntityException(errMessage.toString());
        }
        if(uuidValidator.checkingForIncorrectUUID(messages.get(0).getId().toString())){
            errMessage.append("Incorrect message id. ");
            throw new UnprocessableEntityException(errMessage.toString());
        }


    }


}


package com.AtomIDTest.ForumEngine.util;

import com.AtomIDTest.ForumEngine.models.Message;
import com.AtomIDTest.ForumEngine.models.Topic;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class MessagePaginator {
    public Topic paginate(Topic topic, int page, int size){
        List<Message> messages = topic.getMessages();
        List<Message> res = new ArrayList<>();
        try {
            for (int i = page * size; i <  page * size + size ; i++) {
                res.add(messages.get(i));
            }
        }catch (IndexOutOfBoundsException e){
            throw new InvalidPaginationException("Invalid pagination parameters");
        }
        topic.setMessages(res);
        return topic;
    }
}

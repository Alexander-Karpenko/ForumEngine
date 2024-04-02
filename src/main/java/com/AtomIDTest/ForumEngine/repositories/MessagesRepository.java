package com.AtomIDTest.ForumEngine.repositories;

import com.AtomIDTest.ForumEngine.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessagesRepository extends JpaRepository<Message, UUID> {
}

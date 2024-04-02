package com.AtomIDTest.ForumEngine.repositories;

import com.AtomIDTest.ForumEngine.models.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TopicsRepository extends JpaRepository<Topic, UUID> {
    Optional<Topic> findByName(String name);
    Page<Topic> findAll(Pageable pageable);
}

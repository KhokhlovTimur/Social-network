package ru.itis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.models.Chat;
import ru.itis.models.ChatGlobalId;

import java.util.Optional;

public interface ChatsRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByGlobalId(ChatGlobalId globalId);

    Optional<Chat> findByName(String name);
}

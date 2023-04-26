package ru.itis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.models.ChatGlobalId;

public interface ChatsGlobalIdsRepository extends JpaRepository<ChatGlobalId, Long> {
}

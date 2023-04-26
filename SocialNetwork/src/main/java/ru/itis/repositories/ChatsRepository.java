package ru.itis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.models.Chat;

public interface ChatsRepository extends JpaRepository<Chat, Long> {
}

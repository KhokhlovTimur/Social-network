package ru.itis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.models.Message;

public interface MessagesRepository extends JpaRepository<Message, Long> {
}

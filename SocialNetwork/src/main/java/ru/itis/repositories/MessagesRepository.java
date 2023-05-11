package ru.itis.repositories;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.models.ChatGlobalId;
import ru.itis.models.Message;

import java.util.List;
import java.util.Optional;

public interface MessagesRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByChatGlobalIdOrderBySendingTime(ChatGlobalId chatGlobalId);

    @Query(value = "select * from messages where global_chat_id = :id order by sending_time desc limit 1", nativeQuery = true)
    Optional<Message> findLastMessageByChatGlobalId(@Param("id") Long chatGlobalId);
}

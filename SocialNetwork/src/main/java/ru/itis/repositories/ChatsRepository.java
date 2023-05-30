package ru.itis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.models.Chat;
import ru.itis.models.ChatGlobalId;

import java.util.Optional;

public interface ChatsRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByGlobalId(ChatGlobalId globalId);

    Optional<Chat> findByName(String name);

    @Query(value = "select count(*) from chats join chat_user cu on chats.id = cu.chat_id where chats.global_id = :id", nativeQuery = true)
    int getMembersCount(@Param("id") Long id);
}

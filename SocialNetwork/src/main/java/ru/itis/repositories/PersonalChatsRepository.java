package ru.itis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.models.ChatGlobalId;
import ru.itis.models.PersonalChat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PersonalChatsRepository extends JpaRepository<PersonalChat, Long> {
    @Query(value = "from personal_chats chat where (chat.firstUser.username = :first_username and chat.secondUser.username = :second_username) " +
            "or  (chat.firstUser.username = :second_username and chat.secondUser.username = :first_username) ", nativeQuery = false)
    Optional<PersonalChat> findByFirstUsernameAndSecondUsername(@Param("first_username") String firstUsername, @Param("second_username") String secondUsername);

    Optional<PersonalChat> findByGlobalId(ChatGlobalId globalId);

    @Query(value = "select chat from personal_chats chat where chat.secondUser.username = :username or chat.firstUser.username = :username")
    Set<PersonalChat> findAllByUsername(@Param("username") String username);
}

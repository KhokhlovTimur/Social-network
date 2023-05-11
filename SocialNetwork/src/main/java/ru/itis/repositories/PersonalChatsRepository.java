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
    @Query(value = "select * from personal_chats where first_user_id = :user_id or second_user_id = :user_id", nativeQuery = true)
    Set<PersonalChat> findAllBySecondUserIdOrFirstUserId(@Param("user_id") Long userId);

    Optional<PersonalChat> findByGlobalId(ChatGlobalId globalId);

//    @Query(value = "select * from personal_chats p join users u on p.second_user_id = u.id where u.username like '%:username%'", nativeQuery = true)
//    Optional<PersonalChat> findByFirstUserUsernameOrSecondUserUsername(String username);
}

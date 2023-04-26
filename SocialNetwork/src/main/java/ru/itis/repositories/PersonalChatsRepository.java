package ru.itis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.models.PersonalChat;

public interface PersonalChatsRepository extends JpaRepository<PersonalChat, Long> {
}

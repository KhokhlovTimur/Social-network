package ru.itis.repositories;

import org.springframework.data.repository.query.Param;

public interface GroupsCriteriaRepository {
    boolean isUserExistsInGroup(String username, Long groupId);
}

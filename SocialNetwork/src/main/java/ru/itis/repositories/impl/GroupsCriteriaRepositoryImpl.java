package ru.itis.repositories.impl;

import org.springframework.stereotype.Repository;
import ru.itis.repositories.GroupsCriteriaRepository;

@Repository
public class GroupsCriteriaRepositoryImpl implements GroupsCriteriaRepository {
    @Override
    public boolean isUserExistsInGroup(String username, Long groupId) {
        return false;
    }
}

package ru.itis.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.models.Group;

import java.util.List;

public interface GroupsRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByStatus(Group.Status status);
}

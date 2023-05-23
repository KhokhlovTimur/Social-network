package ru.itis.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.models.Group;
import ru.itis.models.User;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query(value = "select * from users join user_group ug on users.id = ug.user_id where ug.group_id = :id",
            nativeQuery = true)
    Page<User> findAllByGroupId(@Param("id") Long groupId, Pageable pageable);
}

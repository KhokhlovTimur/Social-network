package ru.itis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.models.FriendRequest;

import java.util.List;

public interface FriendsRepository extends JpaRepository<FriendRequest, Long> {
    @Query(value = "select * from friends where second_user_id = :id or " +
            "first_user_id = :id and state = '0'",nativeQuery = true)
    List<FriendRequest> findAllByUserId(@Param("id") Long userId);
}

package ru.itis.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.models.FriendRequest;

import java.util.List;

public interface FriendsRepository extends JpaRepository<FriendRequest, Long> {
    @Query(value = "select friend from friends friend where (friend.firstUser.id = :id or " +
            "friend.secondUser.id = :id) and friend.state = '0' ", nativeQuery = false)
    Page<FriendRequest> findAllByUserId(@Param("id") Long userId, Pageable pageable);
}

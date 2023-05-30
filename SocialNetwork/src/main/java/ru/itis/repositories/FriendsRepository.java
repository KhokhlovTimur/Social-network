package ru.itis.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.models.FriendRequest;

import java.util.List;
import java.util.Optional;

public interface FriendsRepository extends JpaRepository<FriendRequest, Long> {
    @Query(value = "select friend from friends friend " +
            "where (" +
            "       ( friend.firstUser.id = :id and (friend.secondUser.username like concat('%', :query, '%') " +
            "        or friend.secondUser.name like concat('%', :query, '%') or friend.secondUser.surname like concat('%', :query, '%')))" +
            "              or friend.secondUser.id = :id and (friend.firstUser.username like concat('%', :query, '%') " +
            "        or friend.firstUser.name like concat('%', :query, '%') or friend.firstUser.surname like concat('%', :query, '%'))" +
            ") " +
            " and friend.state = '0' ", nativeQuery = false)
    Page<FriendRequest> findAllByUserIdAndUsernameLike(@Param("id") Long userId, @Param("query") String query, Pageable pageable);

    /* */
    @Query(value = "select f from friends f where (f.firstUser.username = :first_username and f.secondUser.username = :second_username and " +
            "  f.state = :state) or (f.firstUser.username = :second_username and f.secondUser.username = :first_username and f.state = :state)", nativeQuery = false)
    Optional<FriendRequest> findByFirstUserUsernameAndSecondUserUsernameAndState(@Param("first_username") String firstUsername, @Param("second_username") String secondUsername,
                                                                                 @Param("state") String state);

    @Query(value = "select f from friends f where (f.firstUser.username = :first_username and f.secondUser.username = :second_username) " +
            " or (f.firstUser.username = :second_username and f.secondUser.username = :first_username)", nativeQuery = false)
    Optional<FriendRequest> findByFirstUserUsernameAndSecondUserUsername(@Param("first_username") String firstUsername, @Param("second_username") String secondUsername);

    @Query(value = "select friend from friends friend " +
            "where (friend.firstUser.id = :id or friend.secondUser.id = :id) and friend.state = '0' ", nativeQuery = false)
    List<FriendRequest> findAllFriendsById(@Param("id") Long id);

}

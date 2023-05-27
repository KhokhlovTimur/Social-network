package ru.itis.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.models.User;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("select user from users user where user.username <> :username and " +
            "(user.username like concat('%', :query, '%') or user.name like concat('%', :query, '%') " +
            "or user.surname like concat('%', :query, '%'))")
    Page<User> getAllByUsernameNotAndUsernameLikeOrNameLikeOrSurnameLike(String username, String query, Pageable pageable);


    @Query(value = "select * from users join user_group ug on users.id = ug.user_id where ug.group_id = :id",
            nativeQuery = true)
    Page<User> findAllByGroupId(@Param("id") Long groupId, Pageable pageable);

    /**
     * if user has requests, he is always second user
     */
    @Query("select friend.firstUser from friends friend where friend.secondUser.username = :username and friend.state = '-1' " +
            " and (friend.firstUser.name like concat('%', :query, '%') or friend.firstUser.surname like concat('%', :query, '%') " +
            "or friend.firstUser.username  like concat('%', :query, '%'))")
    Page<User> getIncomingFriendsRequests(@Param("username") String username, @Param("query") String query, Pageable pageable);

    /**
     * if user sent requests, he is always first user
     */
    @Query("select friend.secondUser from friends friend where friend.firstUser.username = :username and friend.state = '-1" +
            "'  and (friend.secondUser.name like concat('%', :query, '%') or friend.secondUser.surname like concat('%', :query, '%') " +
            "or friend.secondUser.username  like concat('%', :query, '%'))")
    Page<User> getOutgoingFriendsRequests(@Param("username") String username, @Param("query") String query, Pageable pageable);

}

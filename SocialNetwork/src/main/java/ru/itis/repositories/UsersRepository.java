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

    boolean existsByUsername(String username);

    Page<User> getAllByUsernameNot(String username, Pageable pageable);

    Optional<User> findByEmail(String email);

    @Query(value = "select * from users join user_group ug on users.id = ug.user_id where ug.group_id = :id",
            nativeQuery = true)
    Page<User> findAllByGroupId(@Param("id") Long groupId, Pageable pageable);

    /**
     * if user has requests, he is always second user
     */
    @Query("select friend.firstUser from friends friend where friend.firstUser.username = :username and friend.state = '-1' ")
    Page<User> getIncomingFriendsRequests(@Param("username") String username, Pageable pageable);

    /**
     * if user sent requests, he is always first user
     */
    @Query("select friend.secondUser from friends friend where friend.firstUser.username = :username and friend.state = '-1' ")
    Page<User> getOutgoingFriendsRequests(@Param("username") String username, Pageable pageable);

//    @Query(value = " with friend as (select case when f.second_user_id = ?1 then f.first_user_id" +
//            " when f.first_user_id = ?1 then f.second_user_id end " +
//            "as friend_id from friends f)" +
//            " select * from users u join friend on u.id = friend.friend_id where friend.friend_id is not null ", nativeQuery = true)
//    Page<User> getFriends(Long userId,  Pageable pageable);
}

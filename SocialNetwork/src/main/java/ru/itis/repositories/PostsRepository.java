package ru.itis.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.models.Post;

public interface PostsRepository extends JpaRepository<Post, Long> {

    @Query(value = "select post from posts post where post.group is null or post in " +
            " (select post2 from posts post2 join post2.group g " +
            "where exists (select 1 from users u join u.groups gr where u.username = :username and gr.id = g.id)) ")
    Page<Post> findAllByUsername(Pageable pageable, @Param("username") String username);

    Page<Post> findAllByGroupId(Pageable pageable, Long groupId);

    @Query(value = "select count(*) from likes l where post_id = :post_id", nativeQuery = true)
    Long countLikes(@Param("post_id") Long postId);

    @Query(value = "select case when count(*) > 0 then true else false end from " +
            "likes l where post_id = :post_id and user_id = :user_id", nativeQuery = true)
    Boolean isUserPutLikeToPost(@Param("user_id") Long userId, @Param("post_id") Long postId);
}

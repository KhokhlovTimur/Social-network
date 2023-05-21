package ru.itis.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.models.Post;

public interface PostsRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByGroupId(Pageable pageable, Long groupId);

    @Query(value = "select count(*) from likes l where post_id = :post_id", nativeQuery = true)
    Long countLikes(@Param("post_id") Long postId);

    @Query(value = "select case when count(*) > 0 then true else false end from " +
            "likes l where post_id = :post_id and user_id = :user_id", nativeQuery = true)
    Boolean isUserPutLikeToPost(@Param("user_id") Long userId, @Param("post_id") Long postId);

//    @Modifying
//    @Query(value = "delete from post_file where post_id = ?1", nativeQuery = true)
//    void deleteFromPostFile(Long postId);
}

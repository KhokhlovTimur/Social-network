package ru.itis.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.models.Post;

import java.util.List;

public interface PostsRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByGroupIdOrderById(Pageable pageable, Long groupId);

    @Query(value = "select count(*) from likes l join posts p on p.id = l.post_id " +
            "where l.post_id = :post_id and p.group_id = :group_id", nativeQuery = true)
    Long countLikes(@Param("post_id") Long postId, @Param("group_id") Long groupId);
}

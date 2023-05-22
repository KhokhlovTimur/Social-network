package ru.itis.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.models.Group;

import java.util.List;

public interface GroupsRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByStatus(Group.Status status);
    Boolean existsByName(String name);

    @Query(value = "select * from groups g join user_group ug on g.id = ug.group_id " +
            "where ug.user_id = :id", nativeQuery = true)
    Page<Group> findAllByUserId(@Param("id") Long userId, Pageable pageable);

    @Query(value = "select * from groups g where g.name ilike concat('%', :name, '%') ", nativeQuery = true)
    Page<Group> findByNameLike(@Param("name") String name, Pageable pageable);


    @Query(value = "select case when count(*) > 0 then true else false end from groups g join user_group ug " +
            "on g.id = ug.group_id where g.id = :group_id " +
            "and exists (select 1 from users u where u.id = ug.user_id and u.username = :username)", nativeQuery = true)
    boolean isUserExistsInGroup(@Param("username") String username, @Param("group_id") Long groupId);
}

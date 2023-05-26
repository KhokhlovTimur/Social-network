package ru.itis.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.models.ChatGlobalId;

public interface ChatsGlobalIdsRepository extends JpaRepository<ChatGlobalId, Long> {
    @Query(value = "select case when count(*) > 0 then true else false end" +
            " from chats_global_ids cg" +
            " where cg.id = :chat_global_id" +
            "  and (exists(select 1" +
            "              from chats" +
            "                       join chat_user cu on chats.id = cu.chat_id" +
            "              where chats.global_id = cg.id" +
            "                and cu.user_id = :user_id)" +
            "    or exists(select 1" +
            "              from personal_chats pc" +
            "              where pc.global_id = cg.id and (pc.first_user_id = :user_id" +
            "                 or pc.second_user_id = :user_id)))", nativeQuery = true)
    boolean isUserInChat(@Param("chat_global_id") Long chatId, @Param("user_id") Long userId);
}

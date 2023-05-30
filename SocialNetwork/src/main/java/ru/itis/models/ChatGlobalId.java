package ru.itis.models;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import ru.itis.dto.messages.MessageDto;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity(name = "chats_global_ids")
@TypeDef(name = "postgresql_enum", typeClass = PostgreSQLEnumType.class)
public class ChatGlobalId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "chat_type_enum")
    @Type(type = "postgresql_enum")
    private ChatType chatType;

    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "last_msg_id")
    private Message lastMessage;

    public enum ChatType {
        PERSONAL,
        PUBLIC
    }
}

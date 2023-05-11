package ru.itis.models;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TypeDef(name = "postgresql_enum", typeClass = PostgreSQLEnumType.class)
@Entity(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "global_chat_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ChatGlobalId chatGlobalId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "message_type_enum")
    @Type(type = "postgresql_enum")
    private MessageType type;

    private String content;

    @OneToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sendingTime;

    public enum MessageType {
        MESSAGE,
        JOIN,
        LEAVE
    }
}

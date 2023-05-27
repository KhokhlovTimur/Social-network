package ru.itis.models;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity(name = "friends")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "first_user_id")
    private User firstUser;

    @OneToOne
    @JoinColumn(name = "second_user_id")
    private User secondUser;

    private String state;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public enum Status {
        ACCEPTED("0"),
        FIRST_WAIT("-1"),
        SECOND_WAIT("1"),
        NOT_FRIENDS("2");
        private String state;
    }
}

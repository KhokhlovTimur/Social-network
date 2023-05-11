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
@TypeDef(name = "postgresql_enum", typeClass = PostgreSQLEnumType.class)
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

//    @Enumerated(EnumType.STRING)
//    @Column(columnDefinition = "friends_state_enum")
//    @Type(type = "postgresql_enum")
//    private Status state;

    @Getter
    public enum Status {
        ACCEPTED(0),
        FIRST_WAIT(-1),
        SECOND_WAIT(1);
        private int state;

        Status(int state) {
            this.state = state;
        }
    }
}

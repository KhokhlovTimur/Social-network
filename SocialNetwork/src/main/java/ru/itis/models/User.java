package ru.itis.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "users")
@TypeDef(name = "postgresql_enum", typeClass = PostgreSQLEnumType.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String name;
    private String surname;
    private Integer age;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String email;
    private String password;
    private String bio;
    private String gender;
    @Column(name = "date_of_registration")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfRegistration;
    @Column(name = "avatar_link")
    private String avatarLink;

    @ManyToMany(mappedBy = "users", cascade = {CascadeType.MERGE})
    @JsonManagedReference
    private Set<Group> groups;

    @ManyToMany(mappedBy = "usersHaveLiked")
    private Set<Post> likedPosts;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "user_role_enum")
    @Type(type = "postgresql_enum")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "user_state_enum")
    @Type(type = "postgresql_enum")
    private State state;

    @ManyToMany(mappedBy = "users")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Chat> chats;

    public enum Role {
        AUTHORIZED,
        ADMIN,
        SUPER_ADMIN
    }

    public enum State {
        ACTIVE,
        BANNED
    }

    public boolean isBanned() {
        return this.state == State.BANNED;
    }
}

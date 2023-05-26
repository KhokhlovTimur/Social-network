package ru.itis.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(exclude = {"users", "posts", "creator"})
@ToString(exclude = {"users", "posts"})
@Entity(name = "groups")
@TypeDef(name = "postgresql_enum", typeClass = PostgreSQLEnumType.class)
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "image_link")
    private String imageLink;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "group_status")
    @Type(type = "postgresql_enum")
    private Status status;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany
    @JoinTable(name = "user_group", joinColumns = {
            @JoinColumn(name = "group_id", referencedColumnName = "id")
    }, inverseJoinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id")
    })
    @JsonBackReference
    private Set<User> users;

    @OneToMany(mappedBy = "group", cascade = CascadeType.MERGE)
    private Set<Post> posts;

    public enum Status {
        ACTIVE,
        BLOCKED,
        ABANDONED,
        DELETED
    }

    public boolean isBlocked() {
        return status.equals(Status.BLOCKED);
    }

    public boolean isDeleted() {
        return status.equals(Status.DELETED);
    }

    public boolean isActive() {
        return status.equals(Status.ACTIVE);
    }
}

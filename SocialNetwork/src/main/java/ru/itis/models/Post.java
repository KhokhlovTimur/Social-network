package ru.itis.models;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.engine.internal.Cascade;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_of_publication")
    private Date dateOfPublication;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User author;

    @ManyToMany
    @JoinTable(name = "likes", joinColumns = {
            @JoinColumn(name = "post_id", referencedColumnName = "id")
    }, inverseJoinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id")
    })
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> usersHaveLiked;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "post_file", joinColumns = {
            @JoinColumn(name = "post_id")
    },
            inverseJoinColumns = @JoinColumn(name = "file_id"))
    private Set<FileInfo> files;
}

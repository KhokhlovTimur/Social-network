package ru.itis.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "likes", joinColumns = {
            @JoinColumn(name = "post_id", referencedColumnName = "id")
    }, inverseJoinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id")
    })
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> usersHaveLiked;
}

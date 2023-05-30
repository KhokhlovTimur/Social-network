package ru.itis.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "global_id")
    private ChatGlobalId globalId;

    @Column(name = "image_link")
    private String imageLink;

    private String name;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_of_creation")
    private Date dateOfCreation;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "chat_user", joinColumns = {
            @JoinColumn(name = "chat_id", referencedColumnName = "id")
    }, inverseJoinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id")
    })
    private Set<User> users;

}

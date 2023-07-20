package main.server.model.user;

import main.server.model.event.Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import main.server.model.request.Request;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users", schema = "public",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userId;
    @Column(name = "name")
    String name;
    @Column(name = "email")
    String email;
    @OneToMany(
            targetEntity = Event.class,
            mappedBy = "initiator",
            fetch = FetchType.LAZY
    )
    List<Event> events;
    @OneToMany(
            targetEntity = Request.class,
            mappedBy = "requester",
            fetch = FetchType.LAZY
    )
    List<Request> requests;
}

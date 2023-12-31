package main.server.model.event;

import main.server.model.category.Category;
import main.server.model.compilation.Compilation;
import main.server.model.request.Request;
import main.server.model.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long eventId;
    @Column(name = "title")
    String title;
    @Column(name = "annotation")
    @NotBlank
    String annotation;
    @Column(name = "description")
    @NotBlank
    String description;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @Column(name = "created_on")
    LocalDateTime createdOn;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "paid")
    Boolean paid;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    @Column(name = "participation_limit")
    Integer participationLimit;
    @Enumerated(EnumType.STRING)
    EventStatus eventStatus;
    @OneToOne
    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
    Location location;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User initiator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;
    @OneToMany(
            targetEntity = Request.class,
            mappedBy = "event",
            fetch = FetchType.LAZY
    )
    List<Request> requests;
    @ManyToMany(mappedBy = "events")
    Set<Compilation> compilations;
}

package ru.practicum.main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.model.event.Event;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne()
    @JoinColumn(name = "event_id")
    private Event event;
    @ManyToOne()
    @JoinColumn(name = "author_id")
    private User author;
    private LocalDateTime created;
    private Boolean organizer;  // комментарии организатора мероприятия публикуются от имени мероприятия
    @JoinColumn(name = "display_name")
    private String displayName;
}

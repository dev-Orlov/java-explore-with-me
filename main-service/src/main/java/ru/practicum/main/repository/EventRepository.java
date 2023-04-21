package ru.practicum.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.model.event.Event;
import ru.practicum.main.model.event.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByInitiatorId(Long userId, Pageable page);

    Event findByIdAndInitiatorId(Long eventId, Long userId);

    @Query("SELECT e FROM Event e LEFT JOIN e.initiator user LEFT JOIN e.category category " +
            "WHERE ((:users) IS NULL OR user.id IN (:users)) " +
            "AND ((:state) IS NULL OR e.state IN (:state)) " +
            "AND ((:categories) IS NULL OR category.id IN (:categories)) " +
            "AND (e.eventDate BETWEEN (:rangeStart) AND (:rangeEnd))")
    List<Event> findByAdmin(List<Long> users, List<EventState> state, List<Long> categories,
                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);

    @Query("SELECT e FROM Event e LEFT JOIN e.category category " +
            "WHERE ((:text) IS NULL OR ((LOWER(e.annotation) LIKE LOWER(CONCAT('%', (:text), '%'))) " +
            "OR (LOWER(e.description) LIKE LOWER(CONCAT('%', (:text), '%'))))) " +
            "AND ((:categoriesId) IS NULL OR category.id IN (:categoriesId)) " +
            "AND ((:paid) IS NULL OR e.paid = (:paid)) " +
            "AND ((:state) IS NULL OR e.state = (:state)) ")
    Page<Event> findByPublic(String text, List<Long> categoriesId, Boolean paid, EventState state, Pageable page);

    List<Event> findByCategoryId(Long categoryId);
}

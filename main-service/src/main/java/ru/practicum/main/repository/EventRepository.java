package ru.practicum.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.model.event.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByInitiatorId(Long userId, Pageable page);

    Event findByIdAndInitiatorId(Long eventId, Long userId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM events AS e " +
                    "WHERE (?1 IS NULL OR e.initiator_id IN ?1) " +
                    "AND (?2 IS NULL OR e.state IN ?2) " +
                    "AND (?3 IS NULL OR e.category_id IN ?3) " +
                    "AND (e.event_date >= ?4) " +
                    "AND (e.event_date <= ?5) ")
    List<Event> findByAdmin(List<Long> users, List<String> state, List<Long> categories,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);

    @Query(nativeQuery = true,
            value = "SELECT * FROM events AS e " +
                    "WHERE (?1 IS NULL OR ((LOWER(e.annotation) LIKE LOWER(concat('%', ?1, '%'))) " +
                    "OR (LOWER(e.description) LIKE LOWER(concat('%', ?1, '%'))))) " +
                    "AND (?2 IS NULL OR e.category_id IN ?2) " +
                    "AND (?3 IS NULL OR e.paid = ?3) " +
                    "AND (?4 IS NULL OR e.state = ?4) ")
    Page<Event> findByPublic(String text, List<Long> categoriesId, Boolean paid, String state, Pageable page);

    List<Event> findByCategoryId(Long categoryId);
}

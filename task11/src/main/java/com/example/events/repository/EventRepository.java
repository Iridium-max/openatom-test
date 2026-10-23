package com.example.events.repository;
import com.example.events.domain.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import java.time.Instant;
import java.util.Optional;
public interface EventRepository extends JpaRepository<Event, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Event e where e.id = :id")
    Optional<Event> findLocked(@Param("id") Long id);

    @EntityGraph(attributePaths="organizer")
    @Query("""
        select e from Event e where
        (:keyword = '' or lower(e.title) like :pattern escape '!' or lower(e.description) like :pattern escape '!')
        and (:status = '' or (:status = 'UPCOMING' and e.cancelled = false and e.eventTime > :now)
          or (:status = 'ENDED' and e.cancelled = false and e.eventTime <= :now))
        """)
    Page<Event> search(@Param("keyword") String keyword, @Param("pattern") String pattern,
        @Param("status") String status, @Param("now") Instant now, Pageable pageable);
}

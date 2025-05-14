package comeon.playerservice.assignment.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import comeon.playerservice.assignment.entity.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {
        List<Session> findByPlayerIdAndLogoutTimeIsNull(Long playerId);

        Optional<Session> findByIdAndLogoutTimeIsNull(Long id);

        @Query("""
                                SELECT s FROM Session s
                                WHERE s.player.id = :playerId
                                AND s.loginTime >= :startOfDay
                                AND s.loginTime <= :endOfDay
                        """)

        List<Session> findTodaySessionsByPlayerId(@Param("playerId") Long playerId,
                        @Param("startOfDay") LocalDateTime startOfDay,
                        @Param("endOfDay") LocalDateTime endOfDay);

}

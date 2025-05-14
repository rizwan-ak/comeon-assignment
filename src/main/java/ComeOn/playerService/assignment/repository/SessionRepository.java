package comeon.playerservice.assignment.repository;

import comeon.playerservice.assignment.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByPlayerIdAndLogoutTimeIsNull(Long playerId);
}

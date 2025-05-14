package comeon.playerservice.assignment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import comeon.playerservice.assignment.entity.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByPlayerIdAndLogoutTimeIsNull(Long playerId);

    Optional<Session> findByIdAndLogoutTimeIsNull(Long id);
}

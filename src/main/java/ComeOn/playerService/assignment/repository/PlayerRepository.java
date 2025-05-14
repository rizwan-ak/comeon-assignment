package comeon.playerservice.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import comeon.playerservice.assignment.entity.Player;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByEmail(String email);
}
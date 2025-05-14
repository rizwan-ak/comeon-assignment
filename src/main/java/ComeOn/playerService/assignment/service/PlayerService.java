package comeon.playerservice.assignment.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import comeon.playerservice.assignment.dto.LoginRequest;
import comeon.playerservice.assignment.dto.PlayerRegistrationRequest;
import comeon.playerservice.assignment.entity.Player;
import comeon.playerservice.assignment.entity.Session;
import comeon.playerservice.assignment.repository.PlayerRepository;
import comeon.playerservice.assignment.repository.SessionRepository;

@Service
public class PlayerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    private final SessionRepository sessionRepository;
    private final PlayerRepository playerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PlayerService(PlayerRepository playerRepository,
            SessionRepository sessionRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Player registerPlayer(PlayerRegistrationRequest request) {
        logger.info("Registering player with email: {}", request.email);

        if (playerRepository.findByEmail(request.email).isPresent()) {
            logger.error("Email already registered: {}", request.email);
            throw new IllegalArgumentException("Email already registered");
        }

        Player player = Player.builder()
                .email(request.email)
                .password(passwordEncoder.encode(request.password))
                .name(request.name)
                .surname(request.surname)
                .dateOfBirth(request.dateOfBirth)
                .address(request.address)
                .build();

        return playerRepository.save(player);
    }

    public Session login(LoginRequest request) {
        Player player = playerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), player.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        Session session = Session.builder()
                .player(player)
                .loginTime(LocalDateTime.now())
                .build();

        return sessionRepository.save(session);
    }

    public void logout(Long sessionId) {
        Session session = sessionRepository.findByIdAndLogoutTimeIsNull(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Active session not found"));

        session.setLogoutTime(LocalDateTime.now());
        sessionRepository.save(session);

        logger.info("Player {} logged out (session {}).", session.getPlayer().getEmail(), sessionId);
    }
}

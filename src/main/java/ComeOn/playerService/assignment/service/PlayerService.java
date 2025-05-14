package comeon.playerservice.assignment.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import comeon.playerservice.assignment.dto.LoginRequest;
import comeon.playerservice.assignment.dto.PlayerRegistrationRequest;
import comeon.playerservice.assignment.dto.SetTimeLimitRequest;
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
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), player.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        Integer dailyLimit = player.getDailyTimeLimitInMinutes();
        if (dailyLimit != null) {
            // Sum today's session durations
            LocalDateTime start = LocalDate.now().atStartOfDay();
            LocalDateTime end = LocalDateTime.now();
            List<Session> todaySessions = sessionRepository.findTodaySessionsByPlayerId(player.getId(), start, end);

            long totalMinutes = todaySessions.stream().mapToLong(session -> {
                LocalDateTime logout = session.getLogoutTime() != null ? session.getLogoutTime() : end;
                return java.time.Duration.between(session.getLoginTime(), logout).toMinutes();
            }).sum();

            if (totalMinutes >= dailyLimit) {
                throw new IllegalStateException("Daily time limit reached");
            }
        }

        // Automatically log out active players if they exceed the time limit
        sessionRepository.findByPlayerIdAndLogoutTimeIsNull(player.getId()).forEach(activeSession -> {
            LocalDateTime now = LocalDateTime.now();
            long activeMinutes = java.time.Duration.between(activeSession.getLoginTime(), now).toMinutes();
            if (dailyLimit != null && activeMinutes >= dailyLimit) {
                activeSession.setLogoutTime(now);
                sessionRepository.save(activeSession);
                logger.info("Player {} logged out due to reaching daily time limit.", player.getEmail());
            }
        });

        Session session = Session.builder()
                .player(player)
                .loginTime(LocalDateTime.now())
                .build();

        return sessionRepository.save(session);
    }

    public void logout(Long sessionId) {
        Session session = sessionRepository.findByIdAndLogoutTimeIsNull(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session ID"));

        session.setLogoutTime(LocalDateTime.now());
        sessionRepository.save(session);

        logger.info("Player {} logged out (session {}).", session.getPlayer().getEmail(), sessionId);
    }

    public void setTimeLimit(SetTimeLimitRequest request) {
        Player player = playerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        boolean hasActiveSession = sessionRepository
                .findByPlayerIdAndLogoutTimeIsNull(player.getId()).size() > 0;

        if (!hasActiveSession) {
            throw new IllegalStateException("Player is not active");
        }

        player.setDailyTimeLimitInMinutes(request.getDailyLimitMinutes());
        playerRepository.save(player);

        logger.info("Set daily time limit of {} minutes for player {}", request.getDailyLimitMinutes(),
                player.getEmail());
    }

}

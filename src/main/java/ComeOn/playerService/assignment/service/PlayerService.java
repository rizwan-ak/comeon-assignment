package comeon.playerservice.assignment.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import comeon.playerservice.assignment.dto.PlayerRegistrationRequest;
import comeon.playerservice.assignment.entity.Player;
import comeon.playerservice.assignment.repository.PlayerRepository;

@Service
public class PlayerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PlayerService(PlayerRepository playerRepository, BCryptPasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
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
}

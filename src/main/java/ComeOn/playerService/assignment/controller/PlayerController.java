package comeon.playerservice.assignment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comeon.playerservice.assignment.dto.LoginRequest;
import comeon.playerservice.assignment.dto.LogoutRequest;
import comeon.playerservice.assignment.dto.PlayerRegistrationRequest;
import comeon.playerservice.assignment.dto.SetTimeLimitRequest;
import comeon.playerservice.assignment.entity.Player;
import comeon.playerservice.assignment.entity.Session;
import comeon.playerservice.assignment.service.PlayerService;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/register")
    public ResponseEntity<Player> register(@RequestBody PlayerRegistrationRequest request) {
        Player player = playerService.registerPlayer(request);
        return ResponseEntity.ok(player);
    }

    @PostMapping("/login")
    public ResponseEntity<Session> login(@RequestBody LoginRequest request) {
        Session session = playerService.login(request);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequest request) {
        playerService.logout(request.getSessionId());
        return ResponseEntity.ok("Player logged out successfully");
    }

    @PostMapping("/set-daily-limit")
    public ResponseEntity<String> setTimeLimit(@RequestBody SetTimeLimitRequest request) {
        playerService.setTimeLimit(request);
        return ResponseEntity.ok("Time limit set successfully");
    }

}

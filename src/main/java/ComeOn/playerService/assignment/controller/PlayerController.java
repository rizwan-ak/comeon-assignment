package comeon.playerservice.assignment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import comeon.playerservice.assignment.dto.PlayerRegistrationRequest;
import comeon.playerservice.assignment.entity.Player;
import comeon.playerservice.assignment.service.PlayerService;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/register")
    public ResponseEntity<Player> register(@RequestBody PlayerRegistrationRequest playerRegistrationRequest) {
        Player player = playerService.registerPlayer(playerRegistrationRequest);
        return ResponseEntity.ok(player);
    }
}

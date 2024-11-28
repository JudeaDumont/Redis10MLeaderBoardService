package org.example.leaderboardservice.controller;

import org.example.realtimeleaderboard.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @PostMapping("/update")
    public void updateScore(
            @RequestParam String playerId,
            @RequestParam double score,
            @RequestParam String details) {
        leaderboardService.updateScore(playerId, score, details);
    }

    @GetMapping("/top")
    public Set<LeaderboardService.Player> getTopNPlayers(@RequestParam int n) {
        return leaderboardService.getTopNPlayers(n);
    }

    @GetMapping("/{playerId}")
    public LeaderboardService.Player getPlayerRankAndScore(@PathVariable String playerId) {
        return leaderboardService.getPlayerRankAndScore(playerId);
    }
}

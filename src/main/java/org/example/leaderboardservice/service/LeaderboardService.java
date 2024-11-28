package org.example.leaderboardservice.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    private static final String LEADERBOARD_KEY = "game_leaderboard";
    private static final String PLAYER_DETAILS_KEY = "player_details";

    private RedisTemplate<String, String> redisTemplate;

    private ZSetOperations<String, String> zSetOps;
    private BoundHashOperations<String, String, String> hashOps;

    @Autowired
    public LeaderboardService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSetOps = redisTemplate.opsForZSet();
        this.hashOps = redisTemplate.boundHashOps(PLAYER_DETAILS_KEY);
    }

    public void updateScore(String playerId, double score, String details) {
        zSetOps.add(LEADERBOARD_KEY, playerId, score);  // Update score in sorted set
        hashOps.put(playerId, details);  // Update player details in hash
        System.out.printf("Updated score for Player %s: %.2f\n", playerId, score);
    }

    public Set<Player> getTopNPlayers(int n) {
        Set<ZSetOperations.TypedTuple<String>> topPlayers = zSetOps.reverseRangeWithScores(LEADERBOARD_KEY, 0, n - 1);

        return topPlayers.stream()
                .map(tuple -> new Player(
                        tuple.getValue(),
                        tuple.getScore(),
                        hashOps.get(tuple.getValue())
                ))
                .collect(Collectors.toSet());
    }

    public Player getPlayerRankAndScore(String playerId) {
        Long rank = zSetOps.reverseRank(LEADERBOARD_KEY, playerId);
        Double score = zSetOps.score(LEADERBOARD_KEY, playerId);
        String details = hashOps.get(playerId);

        return new Player(playerId, score, details, rank != null ? rank + 1 : null);
    }

    @Getter
    @Setter
    public static class Player {
        private String playerId;
        private Double score;
        private String details;
        private Long rank;

        public Player(String playerId, Double score, String details) {
            this.playerId = playerId;
            this.score = score;
            this.details = details;
        }

        public Player(String playerId, Double score, String details, Long rank) {
            this(playerId, score, details);
            this.rank = rank;
        }
    }
}

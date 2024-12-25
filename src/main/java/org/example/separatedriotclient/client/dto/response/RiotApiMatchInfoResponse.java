package org.example.separatedriotclient.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.separatedriotclient.client.enums.QueueType;

import java.util.Map;

@Getter
@AllArgsConstructor
public class RiotApiMatchInfoResponse {

    private final int wins;
    private final int losses;
    private final int totalGames;
    private final QueueType queueType;

    private final double winRate;
    private final double averageKill;
    private final double averageDeath;
    private final double averageAssist;

    private final Map<String, Integer> champCountMap;
    private final Map<String, Integer> winCountMap;
}

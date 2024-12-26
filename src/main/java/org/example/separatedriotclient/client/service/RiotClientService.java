package org.example.separatedriotclient.client.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.separatedriotclient.client.dto.request.*;
import org.example.separatedriotclient.client.dto.response.*;
import org.example.separatedriotclient.client.enums.QueueType;
import org.example.separatedriotclient.client.repository.VersionRepository;
import org.example.separatedriotclient.common.util.TimeUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.separatedriotclient.client.enums.QueueType.*;

@Service
@RequiredArgsConstructor
public class RiotClientService {

    private final RiotClient riotClient;
    private final TimeUtil timeUtil;
    private final VersionRepository versionRepository;

    private static final int NUMBER_OF_RECENT_MATCH = 20;
    private static final int MAX_METHOD_CALL = 100;

    public RiotApiAccountInfoResponse getAccountInfos(RiotApiAccountInfoRequest request) {
        PuuidResponse puuidResponse = riotClient.extractPuuid(
                request.getSummonerName(),
                request.getTagLine(),
                request.getRegion()
        );

        SummonerResponse summonerResponse = riotClient.extractSummonerInfo(
                puuidResponse.getPuuid(),
                request.getServer()
        );

        return RiotApiAccountInfoResponse.of(
                puuidResponse.getPuuid(),
                summonerResponse.getAccountId(),
                summonerResponse.getId()
        );
    }

    public String getProfileIconUrl(RiotApiUpdateProfileRequest request) {
        SummonerResponse response = riotClient.extractSummonerInfo(request.getPuuid(), request.getServer());
        int accountProfileIconId = response.getProfileIconId();

        String latestVersion = versionRepository.findLatestVersion().getVersionNumber();

        return String.format(
                "https://ddragon.leagueoflegends.com/cdn/%s/img/profileicon/%d.png",
                latestVersion, accountProfileIconId
        );
    }

    public RiotApiRankInfoResponse getRankInfos(RiotApiRankInfoRequest request) {
        List<LeagueEntryResponse> leagueInfoList = riotClient.extractLeagueInfo(request.getSummonerId(), request.getServer());

        int soloTotalGames = 0, flexTotalGames = 0;
        int soloWins = 0, soloLosses = 0;
        int flexWins = 0, flexLosses = 0;
        String soloTier = "", soloRank = "";
        String flexTier = "", flexRank = "";

        for (LeagueEntryResponse leagueInfo : leagueInfoList) {
            int wins = leagueInfo.getWins();
            int losses = leagueInfo.getLosses();

            if (leagueInfo.getQueueType().equals(SOLO.getQueueType())) {
                soloWins = wins;
                soloLosses = losses;
                soloTotalGames = wins + losses;
                soloTier = leagueInfo.getTier();
                soloRank = leagueInfo.getRank();
            }
            else if (leagueInfo.getQueueType().equals(FLEX.getQueueType())) {
                flexWins = wins;
                flexLosses = losses;
                flexTotalGames = wins + losses;
                flexTier = leagueInfo.getTier();
                flexRank = leagueInfo.getRank();
            }
        }

        double soloWinRate = 0;
        double flexWinRate = 0;
        if ((soloWins + soloLosses) > 0) {
            soloWinRate = (double) soloWins / (soloWins + soloLosses) * 100;
        }
        if ((flexWins + flexLosses) > 0) {
            flexWinRate = (double) flexWins / (flexWins + flexLosses) * 100;
        }

        return new RiotApiRankInfoResponse(soloTotalGames, flexTotalGames, soloWins, soloLosses, soloWinRate, soloTier, soloRank, flexWins, flexLosses, flexWinRate, flexTier, flexRank);
    }

    public List<String> getMatchIds(RiotApiMatchIdRequest request) {
        List<String> matchIds = new ArrayList<>();
        QueueType queueType = request.getQueueType();
        int totalRetrieved = 0;

        if (queueType == QUICK) {
            int start = 0;
            while (true) {
                List<String> partialMatchIds = riotClient.extractMatchIds(null, null, queueType.getQueueId(), null, start, 100, request.getRegion(), request.getPuuid());
                if (partialMatchIds == null || partialMatchIds.isEmpty()) {
                    break;
                }

                matchIds.addAll(partialMatchIds);
                totalRetrieved += partialMatchIds.size();
                start += totalRetrieved;
            }
        } else {
            while (totalRetrieved < request.getPlayCount()) {
                int count = Math.min(MAX_METHOD_CALL, request.getPlayCount() - totalRetrieved);
                List<String> partialMatchIds = riotClient.extractMatchIds(
                        null, null,
                        queueType.getQueueId(),
                        null, totalRetrieved,
                        count, request.getRegion(), request.getPuuid()
                );

                if (partialMatchIds == null || partialMatchIds.isEmpty()) {
                    break;
                }

                matchIds.addAll(partialMatchIds);
                totalRetrieved += partialMatchIds.size();

                // 만약 가져온 판 수가 예상보다 적다면 더 이상 호출할 필요가 없음
                if (partialMatchIds.size() < count) {
                    break;
                }
            }
        }

        return matchIds;
    }

    // 수동으로 전적 정보 갱신 시 하루 안에 100번 게임을 할 가능성이 없다고 판단했습니다.
    // 따라서 RiotClient의 getmatchIds를 한번만 호출해도 필요한 모든 정보를 다 조회할 수 있다고 생각해서, 1번만 호출하게 되었습니다.
    public List<String> updateMatchIds(RiotApiUpdateMatchIdRequest request) {
        long startTime = timeUtil.convertToEpochSeconds(request.getLastUpdatedAt());
        return riotClient.extractMatchIds(
                startTime, null, request.getQueueType().getQueueId(), null, 0, 100, request.getRegion(), request.getPuuid()
        );
    }

    @Transactional
    public RiotApiMatchInfoResponse getMatchStats(RiotApiMatchInfoRequest request) {
        // 초기화
        int totalKills = 0, totalDeath = 0, totalAssists = 0;
        int winCount = 0;
        List<String> matchIds = request.getMatchIds();
        int totalGames = matchIds.size();
        Map<String, Integer> champCountMap = new HashMap<>();
        Map<String, Integer> winCountMap = new HashMap<>();

        // 20 게임 제한을 위한 서브 리스트
        List<String> limitedMatchIds = totalGames > NUMBER_OF_RECENT_MATCH ? matchIds.subList(0, NUMBER_OF_RECENT_MATCH) : matchIds;

        for (String matchId : matchIds) {
            FormattedMatchResponse matchResponse = riotClient.getMatchDetails(matchId, request.getSummonerName(), request.getTagLine(), request.getRegion());

            if (matchResponse != null) {
                String championName = matchResponse.getChampionName();
                champCountMap.put(championName, champCountMap.getOrDefault(championName, 0) + 1);

                if (matchResponse.isWin()) {
                    winCount++;
                    winCountMap.put(championName, winCountMap.getOrDefault(championName, 0) + 1);
                }
            }
        }
        double winRate = 0;
        if (request.getQueueType().equals(QUICK) && totalGames > 0) {
            winRate = (double) winCount / totalGames * 100;
        }

        // 최근 20경기에 대해 KDA 계산
        for (String matchId : limitedMatchIds) {
            FormattedMatchResponse matchResponse = riotClient.getMatchDetails(matchId, request.getSummonerName(), request.getTagLine(), request.getRegion());

            if (matchResponse != null) {
                totalKills += matchResponse.getKills();
                totalDeath += matchResponse.getDeaths();
                totalAssists += matchResponse.getAssists();
            }
        }

        double averageKill = (double) totalKills / totalGames;
        double averageDeath = (double) totalDeath / totalGames;
        double averageAssist = (double) totalAssists / totalGames;

        return new RiotApiMatchInfoResponse(
                winCount, totalGames - winCount, totalGames, request.getQueueType(),
                winRate, averageKill, averageDeath, averageAssist, champCountMap, winCountMap
        );
    }
}

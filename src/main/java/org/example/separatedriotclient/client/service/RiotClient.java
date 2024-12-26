package org.example.separatedriotclient.client.service;

import lombok.RequiredArgsConstructor;
import org.example.separatedriotclient.client.dto.response.FormattedMatchResponse;
import org.example.separatedriotclient.client.dto.response.LeagueEntryResponse;
import org.example.separatedriotclient.client.dto.response.PuuidResponse;
import org.example.separatedriotclient.client.dto.response.SummonerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RiotClient {

    private final RestTemplate restTemplate;
    private final Map<String, String> regionBaseUrls = Map.of(
            "ASIA", "https://asia.api.riotgames.com",
            "AMERICAS", "https://americas.api.riotgames.com",
            "EUROPE", "https://europe.api.riotgames.com"
    );

    @Value("${riot.api.key}")
    private String apiKey;

    public PuuidResponse extractPuuid(String summonerName, String tagLine, String region) {
        // Choose regional URL
        String baseUrl = regionBaseUrls.getOrDefault(region, null);
        if (baseUrl == null) {
            throw new IllegalArgumentException("Invalid region specified: " + region);
        }

        // Set request URL
        String url = String.format(
                "%s/riot/account/v1/accounts/by-riot-id/%s/%s?api_key=%s",
                baseUrl, summonerName, tagLine, apiKey
        );

        // Execute request
        return restTemplate.getForObject(url, PuuidResponse.class);
    }

    public SummonerResponse extractSummonerInfo(String puuid, String server) {
        // Server code
        String serverDomain = server.toLowerCase();

        // Set request URL
        String url = String.format(
                "https://%s.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/%s?api_key=%s",
                serverDomain, puuid, apiKey
        );

        // Execute request
        return restTemplate.getForObject(url, SummonerResponse.class);
    }

    public List<LeagueEntryResponse> extractLeagueInfo(String summonerId, String server) {
        String serverDomain = server.toLowerCase();

        String url = String.format(
                "https://%s.api.riotgames.com/lol/league/v4/entries/by-summoner/%s?api_key=%s",
                serverDomain, summonerId, apiKey
        );
        try {
            LeagueEntryResponse[] responseArray = restTemplate.getForObject(url, LeagueEntryResponse[].class);
            if (responseArray == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(responseArray));
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch league info from API", e);
        }
    }

    public List<String> extractMatchIds(Long startTime, Long endTime, Integer queue, String type,
                                        Integer start, Integer count, String region, String puuid) {
        // Choose regional URL
        String baseUrl = regionBaseUrls.getOrDefault(region, null);
        if (baseUrl == null) {
            throw new IllegalArgumentException("Invalid region specified: " + region);
        }

        // Build URL dynamically
        StringBuilder urlBuilder = new StringBuilder(
                String.format("%s/lol/match/v5/matches/by-puuid/%s/ids?", baseUrl, puuid)
        );

        if (startTime != null) {
            urlBuilder.append("startTime=").append(startTime).append("&");
        }
        if (endTime != null) {
            urlBuilder.append("endTime=").append(endTime).append("&");
        }
        if (queue != null) {
            urlBuilder.append("queue=").append(queue).append("&");
        }
        if (type != null && !type.isEmpty()) {
            urlBuilder.append("type=").append(type).append("&");
        }
        if (start != null) {
            urlBuilder.append("start=").append(start).append("&");
        } else {
            urlBuilder.append("start=0&");
        }
        if (count != null) {
            urlBuilder.append("count=").append(count).append("&");
        } else {
            urlBuilder.append("count=20&");
        }

        urlBuilder.append("api_key=").append(apiKey);

        String url = urlBuilder.toString();

        ResponseEntity<List<String>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    public FormattedMatchResponse getMatchDetails(String matchId, String summonerName, String tagLine, String region) {
        //        // Choose regional URL
//        String baseUrl = regionBaseUrls.getOrDefault(region.toString(), null);
//        if (baseUrl == null) {
//            throw new IllegalArgumentException("Invalid region specified: " + region);
//        }
//
//        // Set request URL
//        String url = String.format(
//                "%s/lol/match/v5/matches/%s?api_key=%s",
//                baseUrl, matchId, apiKey
//        );
//
//        MatchResponse matchResponse = restTemplate.getForObject(url, MatchResponse.class);
//        if (matchResponse == null || matchResponse.getInfo() == null || matchResponse.getInfo().getParticipants() == null) {
//            throw new IllegalArgumentException("Invalid match specified: " + matchId);
//        }
//
//        // Filter participants
//        return matchResponse.getInfo().getParticipants().stream()
//                .filter(p -> summonerName.equals(p.getRiotIdGameName()) && tagLine.equals(p.getRiotIdTagline()))
//                .map(target -> new FormattedMatchResponse(
//                        target.getChampionName(),
//                        target.getKills(),
//                        target.getDeaths(),
//                        target.getAssists(),
//                        target.isWin()
//                ))
//                .findFirst()
//                .orElseThrow(
//                        () -> new IllegalArgumentException("No matching participant found in the match")
//                );

        // 직접 제작한 Lambda Function 으로 랜덤한 매치 데이터를 받습니다.
        String baseUrl = "https://oprimofm4f.execute-api.ap-northeast-2.amazonaws.com/default/getMatchDetails";

        ResponseEntity<FormattedMatchResponse> response = restTemplate.getForEntity(baseUrl, FormattedMatchResponse.class);
        return response.getBody();
    }
}

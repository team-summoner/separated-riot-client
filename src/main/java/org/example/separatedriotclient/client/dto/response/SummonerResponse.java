package org.example.separatedriotclient.client.dto.response;

import lombok.Getter;

@Getter
public class SummonerResponse {
    private String accountId;
    private int profileIconId;
    private long revisionDate;
    private String id;
    private String puuid;
    private long summonerLevel;
}

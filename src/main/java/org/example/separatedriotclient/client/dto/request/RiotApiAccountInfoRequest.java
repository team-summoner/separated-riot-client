package org.example.separatedriotclient.client.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RiotApiAccountInfoRequest {

    @NotNull
    private String accountType;

    @NotNull
    private String accountId;

    @NotNull
    private String accountPassword;

    @NotNull
    private String summonerName;

    @NotNull
    private String tagLine;

    @NotNull
    private String server;

    @NotNull
    private String region;
}

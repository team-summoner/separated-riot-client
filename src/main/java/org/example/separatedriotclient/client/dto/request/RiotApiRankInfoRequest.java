package org.example.separatedriotclient.client.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RiotApiRankInfoRequest {

    @NotNull
    private String summonerId;

    @NotNull
    private String server;
}
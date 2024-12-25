package org.example.separatedriotclient.client.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RiotApiUpdateProfileRequest {

    private String puuid;

    private String server;
}

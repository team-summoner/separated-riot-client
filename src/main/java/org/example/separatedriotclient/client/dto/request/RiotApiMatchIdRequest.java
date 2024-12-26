package org.example.separatedriotclient.client.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.separatedriotclient.client.enums.QueueType;

@Getter
@RequiredArgsConstructor
public class RiotApiMatchIdRequest {

    @NotNull
    private QueueType queueType;

    private int playCount;

    @NotNull
    private String region;

    @NotNull
    private String puuid;
}

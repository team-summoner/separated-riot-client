package org.example.separatedriotclient.client.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.separatedriotclient.client.enums.QueueType;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class RiotApiUpdateMatchIdRequest {

    @NotNull
    private QueueType queueType;

    @NotNull
    private LocalDateTime lastUpdatedAt;

    @NotNull
    private String region;

    @NotNull
    private String puuid;
}

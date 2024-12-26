package org.example.separatedriotclient.client.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.separatedriotclient.client.enums.QueueType;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RiotApiMatchInfoRequest {

    @NotNull
    private Long accountId;

    private List<String> matchIds;

    @NotNull
    private QueueType queueType;

    @NotNull
    private String summonerName;

    @NotNull
    private String tagLine;

    @NotNull
    private String region;
}

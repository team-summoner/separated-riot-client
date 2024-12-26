package org.example.separatedriotclient.client.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MatchResponse {
    private final MetadataResponse metadata;
    private final InfoResponse info;
}

package org.example.separatedriotclient.client.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FormattedMatchResponse {
    private final String championName;
    private final int kills;
    private final int deaths;
    private final int assists;
    private final boolean win;
}


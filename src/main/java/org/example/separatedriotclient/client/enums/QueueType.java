package org.example.separatedriotclient.client.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QueueType {
    QUICK("QUICK", 490),
    SOLO("RANKED_SOLO_5x5", 420),
    FLEX("RANKED_FLEX_SR", 440);

    private final String queueType;
    private final Integer queueId;
}

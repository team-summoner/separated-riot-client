package org.example.separatedriotclient.client.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.separatedriotclient.client.service.DataDragonService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataDragonScheduler {

    private final DataDragonService dataDragonService;

    @Scheduled(cron = "0 0 0 * * MON")
    public void updateDataDragonInfo() {
        dataDragonService.execute();
    }
}


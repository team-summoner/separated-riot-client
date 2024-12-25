package org.example.separatedriotclient.client.controller;

import lombok.RequiredArgsConstructor;
import org.example.separatedriotclient.client.service.DataDragonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/riot-client")
@RequiredArgsConstructor
public class DataDragonController {
    private final DataDragonService dataDragonService;

    @GetMapping("/ddragon")
    public ResponseEntity<Void> updateDataDragonInfo() {
        dataDragonService.execute();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

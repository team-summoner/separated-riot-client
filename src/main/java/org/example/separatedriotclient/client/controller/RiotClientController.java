package org.example.separatedriotclient.client.controller;

import lombok.RequiredArgsConstructor;
import org.example.separatedriotclient.client.dto.request.*;
import org.example.separatedriotclient.client.dto.response.RiotApiAccountInfoResponse;
import org.example.separatedriotclient.client.dto.response.RiotApiMatchInfoResponse;
import org.example.separatedriotclient.client.dto.response.RiotApiRankInfoResponse;
import org.example.separatedriotclient.client.service.RiotClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/riot-client")
@RequiredArgsConstructor
public class RiotClientController {

    private final RiotClientService riotClientService;

    @PostMapping("/account-infos")
    public ResponseEntity<RiotApiAccountInfoResponse> getAccountInfos(@RequestBody RiotApiAccountInfoRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(riotClientService.getAccountInfos(request));
    }

    @PostMapping("/update-profile-icon")
    public ResponseEntity<String> getProfileIcon(@RequestBody RiotApiUpdateProfileRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(riotClientService.getProfileIconUrl(request));
    }

    @PostMapping("/rank-infos")
    public ResponseEntity<RiotApiRankInfoResponse> getRankInfos(@RequestBody RiotApiRankInfoRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(riotClientService.getRankInfos(request));
    }

    @PostMapping("/match-ids")
    public ResponseEntity<List<String>> getMatchIds(@RequestBody RiotApiMatchIdRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(riotClientService.getMatchIds(request));
    }

    @PutMapping("/match-ids")
    public ResponseEntity<List<String>> updateMatchIds(@RequestBody RiotApiUpdateMatchIdRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(riotClientService.updateMatchIds(request));
    }

    @PostMapping("/match-infos")
    public ResponseEntity<RiotApiMatchInfoResponse> getMatchInfos(@RequestBody RiotApiMatchInfoRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(riotClientService.getMatchStats(request));
    }
}

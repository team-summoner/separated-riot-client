package org.example.separatedriotclient.client.service;

import lombok.RequiredArgsConstructor;
import org.example.separatedriotclient.client.entity.Champion;
import org.example.separatedriotclient.client.entity.ProfileIcon;
import org.example.separatedriotclient.client.entity.Version;
import org.example.separatedriotclient.client.repository.ChampionRepository;
import org.example.separatedriotclient.client.repository.ProfileIconRepository;
import org.example.separatedriotclient.client.repository.VersionRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataDragonService {

    private final VersionRepository versionRepository;
    private final ChampionRepository championRepository;
    private final ProfileIconRepository profileIconRepository;
    private final RestTemplate restTemplate;

    public void execute() {
        String latestVersion = fetchLatestVersion();
        saveVersion(latestVersion);
        updateChampions(latestVersion);
        updateProfileIcons(latestVersion);
    }

    public String fetchLatestVersion() {
        String url = "https://ddragon.leagueoflegends.com/api/versions.json";
        ResponseEntity<List<String>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody().get(0);
    }

    public void saveVersion(String version) {
        Version latestVersion = Version.of(version);
        versionRepository.save(latestVersion);
    }

    public void updateChampions(String version) {
        String url = String.format(
                "https://ddragon.leagueoflegends.com/cdn/%s/data/ko_KR/champion.json",
                version
        );

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.get("data");

        // Delete original data
        championRepository.deleteAll();

        List<Champion> champions = data.values().stream()
                .map(champData -> {
                    Map<String, Object> champMap = (Map<String, Object>) champData;
                    return Champion.of(
                            champMap.get("id").toString(),
                            champMap.get("name").toString()
                    );
                })
                .toList();

        championRepository.saveAll(champions);
    }

    public void updateProfileIcons(String version) {
        String url = String.format(
                "https://ddragon.leagueoflegends.com/cdn/%s/data/ko_KR/profileicon.json",
                version
        );

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.get("data");

        // Delete original data
        profileIconRepository.deleteAll();

        List<ProfileIcon> icons = data.values().stream()
                .map(iconData -> {
                    Map<String, Object> iconMap = (Map<String, Object>) iconData;
                    return ProfileIcon.of(
                            Integer.parseInt(iconMap.get("id").toString())
                    );
                })
                .toList();

        profileIconRepository.saveAll(icons);
    }
}

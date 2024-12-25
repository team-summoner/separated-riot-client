package org.example.separatedriotclient.client.repository;

import org.example.separatedriotclient.client.entity.Champion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChampionRepository extends JpaRepository<Champion, Long> {
}

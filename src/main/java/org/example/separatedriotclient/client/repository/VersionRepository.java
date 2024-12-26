package org.example.separatedriotclient.client.repository;

import org.example.separatedriotclient.client.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VersionRepository extends JpaRepository<Version, Long> {

    @Query("SELECT v FROM Version v ORDER By v.id DESC")
    Version findLatestVersion();
}

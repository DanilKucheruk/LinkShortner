package com.linkshortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.linkshortener.model.UniqueLinkClicks;
import java.util.Optional;
import com.linkshortener.model.Link;

@Repository
public interface UniqueLinkClicksRepository extends JpaRepository<UniqueLinkClicks, Long> {
    Optional<UniqueLinkClicks> findByLinkAndSha(Link link, String sha);

    Long countByLink(Link link);
}

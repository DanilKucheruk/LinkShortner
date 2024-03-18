package com.linkshortener.repository;

import org.springframework.stereotype.Repository;

import com.linkshortener.model.Link;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByLinkRedirect(String linkRedirect);

    Optional<Link> findByLinkRedirectAndUserSha(String href, String sha1);

    Optional<Link> findByGeneratedValueAndUserSha(String generated, String sha1);

    Optional<Link> findByGeneratedValue(String generatedValue);
}

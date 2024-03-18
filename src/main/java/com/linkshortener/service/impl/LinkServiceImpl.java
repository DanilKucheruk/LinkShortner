package com.linkshortener.service.impl;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Random;

import java.time.LocalDateTime;
import com.linkshortener.model.Link;
import com.linkshortener.model.UniqueLinkClicks;
import com.linkshortener.repository.LinkRepository;
import com.linkshortener.repository.UniqueLinkClicksRepository;
import com.linkshortener.service.LinkService;
import com.linkshortener.util.Sha1Generator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {
  private final LinkRepository linkRepository;
  private final UniqueLinkClicksRepository uniqueLinkClicksRepository;
  private final Sha1Generator sha1Generator;

  @Override
  @SneakyThrows
  public String saveOrGetLink(String href, Double time, String key, HttpServletRequest request) {
    String sh1 = sha1Generator.generate(key, request);
    try {
      Link link = linkRepository.findByLinkRedirectAndUserSha(href, sh1).get();
      checkTimeLink(link);
      return link.getGeneratedValue();
    } catch (NotFoundException | NoSuchElementException e) {
      Link link = new Link();
      link.setGeneratedValue(getRandomString(5));
      link.setLinkRedirect(href);
      link.setUserSha(sh1);
      if (time != null) {
        link.setTime(time);
      }
      return linkRepository.save(link).getGeneratedValue();
    }
  }

  @Override
  @SneakyThrows
  public String getLinkByGeneratedToStats(String generated, String key, HttpServletRequest request) {
    Link link = linkRepository.findByGeneratedValueAndUserSha(generated, sha1Generator.generate(key, request))
        .orElseThrow(NotFoundException::new);
    checkTimeLink(link);
    return "Редирект на: " + link.getLinkRedirect()
        + " Переходы: " + link.getCountClick() + " Уникальные переходы: "
        + uniqueLinkClicksRepository.countByLink(link);
  }

  @Override
  @Transactional
  @SneakyThrows
  public Link getLinkByGeneratedValue(String generated, HttpServletRequest request) {
    return linkRepository.findByGeneratedValue(generated)
        .map(link -> {
          String sha = sha1Generator.generate("", request);
          link.setCountClick(link.getCountClick() + 1L);
          if (!uniqueLinkClicksRepository.findByLinkAndSha(link, sha).isPresent()) {
            UniqueLinkClicks unique = new UniqueLinkClicks();
            unique.setLink(link);
            unique.setSha(sha);
            uniqueLinkClicksRepository.save(unique);
          }
          try {
            checkTimeLink(link);
          } catch (NotFoundException e) {
            e.printStackTrace();
          }
          return link;
        })
        .orElseThrow(NotFoundException::new);
  }

  @Override
  @Transactional
  @SneakyThrows
  public void deleteLinkByGeneratedValue(String generated, String key, HttpServletRequest request) {
    Link link = linkRepository.findByGeneratedValueAndUserSha(generated, sha1Generator.generate(key, request))
        .orElseThrow(NotFoundException::new);
    linkRepository.delete(link);
  }

  private void checkTimeLink(Link link) throws NotFoundException {
    if (link.getTime() != null) {
      Long minutes = (long) (link.getTime() * 60L);
      if (LocalDateTime.now().minusMinutes(minutes).compareTo(link.getCreateDate()) != -1) {
        linkRepository.delete(link);
        throw new NotFoundException();
      }
    }
  }

  private static String getRandomString(int length) {
    String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    Random random = new Random();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(str.length() - 1);
      sb.append(str.charAt(number));
    }
    return sb.toString();
  }
}

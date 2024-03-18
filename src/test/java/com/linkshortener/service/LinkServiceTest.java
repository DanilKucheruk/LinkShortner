package com.linkshortener.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.linkshortener.model.Link;
import com.linkshortener.model.UniqueLinkClicks;
import com.linkshortener.repository.LinkRepository;
import com.linkshortener.repository.UniqueLinkClicksRepository;
import com.linkshortener.service.impl.LinkServiceImpl;
import com.linkshortener.util.Sha1Generator;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {
    @InjectMocks
    private LinkServiceImpl linkService;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private UniqueLinkClicksRepository uniqueLinkClicksRepository;

    @Mock
    private Sha1Generator sha1Generator;

    @Mock
    private HttpServletRequest request;

    private Link link;

    @BeforeEach
    public void setUp() {
        link = new Link();
        link.setGeneratedValue("generated");
        link.setLinkRedirect("https://test.com");
        link.setUserSha("sha");
        link.setCreateDate(LocalDateTime.now());
        link.setCountClick(0L);
    }

    @Test
    void saveOrGetLink_successful_new_Link() {
        when(sha1Generator.generate(any(), any())).thenReturn("sha");
        when(linkRepository.findByLinkRedirectAndUserSha(any(), any())).thenReturn(Optional.empty());
        when(linkRepository.save(any())).thenReturn(link);

        String result = linkService.saveOrGetLink("https://test.com", null, "", request);

        assertEquals("generated", result);
        verify(linkRepository, times(1)).save(any());
    }

    @Test
    void saveOrGetLink_existingLink() {
        when(sha1Generator.generate(any(), any())).thenReturn("sha");
        when(linkRepository.findByLinkRedirectAndUserSha(any(), any())).thenReturn(Optional.of(link));

        String result = linkService.saveOrGetLink("https://test.com", null, "", request);
        assertEquals("generated", result);
        verify(linkRepository, never()).save(any());
    }

    @Test
    void getLinkByGeneratedToStats_invalidGeneratedValue() {
        when(sha1Generator.generate(any(), any())).thenReturn("sha");
        when(linkRepository.findByGeneratedValueAndUserSha(any(), any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> linkService.getLinkByGeneratedToStats("invalid", "", request));
    }

    @Test
    void getLinkByGeneratedToStats_validGeneratedValue() {
        when(sha1Generator.generate(any(), any())).thenReturn("sha");
        when(linkRepository.findByGeneratedValueAndUserSha(any(), any())).thenReturn(Optional.of(link));

        String expectedResult = "Редирект на: https://test.com Переходы: 0 Уникальные переходы: 0";

        String result = linkService.getLinkByGeneratedToStats("generated", "", request);

        assertEquals(expectedResult, result);
    }

    @Test
    void getLinkByGeneratedValue_validGeneratedValue() {
        when(sha1Generator.generate(any(), any())).thenReturn("sha");
        when(linkRepository.findByGeneratedValue(any())).thenReturn(Optional.of(link));
        when(uniqueLinkClicksRepository.findByLinkAndSha(any(), any())).thenReturn(Optional.empty());

        Link result = linkService.getLinkByGeneratedValue("generated", request);

        assertEquals("generated", result.getGeneratedValue());
        assertEquals("https://test.com", result.getLinkRedirect());
        assertEquals("sha", result.getUserSha());
        verify(uniqueLinkClicksRepository, times(1)).save(any(UniqueLinkClicks.class));

    }

    @Test
    void deleteLinkByGeneratedValue_successful() {
        when(linkRepository.findByGeneratedValueAndUserSha(any(), any())).thenReturn(Optional.of(link));

        linkService.deleteLinkByGeneratedValue("generate", "", request);

        verify(linkRepository, times(1)).delete(any(Link.class));
    }

}
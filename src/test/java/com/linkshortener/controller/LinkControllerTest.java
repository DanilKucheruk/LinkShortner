package com.linkshortener.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.AccessDeniedException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.linkshortener.model.Link;
import com.linkshortener.service.impl.LinkServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class LinkControllerTest {
    @InjectMocks
    private LinkController linkController;

    @Mock
    private LinkServiceImpl linkService;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        linkService = mock(LinkServiceImpl.class);
        linkController = new LinkController(linkService);
    }

    @Test
    public void testGenerateLink_successful() {
        String redirectLink = "testLink";
        Double time = 60.0;
        String key = "testKey";
        String generatedValue = "generated";

        when(linkService.saveOrGetLink(redirectLink, time, key, request)).thenReturn(generatedValue);

        ResponseEntity<String> result = linkController.generateLink(redirectLink, time, key, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(generatedValue, result.getBody());
    }

    @Test
    public void testRedirectLink_successful() {
        String generated = "generated";
        String redirectLink = "testLink";
        Link link = new Link();
        link.setLinkRedirect(redirectLink);

        when(linkService.getLinkByGeneratedValue(generated, request)).thenReturn(link);

        ResponseEntity<?> result = linkController.redirectLink(generated, request);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
        assertTrue(result.getHeaders().containsKey("Location"));
        assertEquals(redirectLink, result.getHeaders().get("Location").get(0));
    }

    @Test
    public void testStatsLink_successful() {
        String generated = "generated";
        String key = "key";
        String stats = "testStats";

        when(linkService.getLinkByGeneratedToStats(generated, key, request)).thenReturn(stats);

        ResponseEntity<String> result = linkController.statsLink(generated, key, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(stats, result.getBody());
    }

    @Test
    public void testDeleteLink_successful() {
        String generated = "generated";
        String key = "key";

        doNothing().when(linkService).deleteLinkByGeneratedValue(generated, key, request);

        String result = linkController.deleteLink(generated, request, key);

        assertEquals("Short link deleted", result);
    }
}

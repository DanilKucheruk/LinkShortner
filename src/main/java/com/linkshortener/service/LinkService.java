package com.linkshortener.service;

import com.linkshortener.model.Link;

import jakarta.servlet.http.HttpServletRequest;

public interface LinkService {
    Link getLinkByGeneratedValue(String generated, HttpServletRequest request);
    String saveOrGetLink(String href, Double time, String key, HttpServletRequest request);
    void deleteLinkByGeneratedValue(String generated, String key, HttpServletRequest request);
    String getLinkByGeneratedToStats(String generated, String key, HttpServletRequest request);
    
} 

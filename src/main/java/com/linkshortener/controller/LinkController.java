package com.linkshortener.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.linkshortener.service.LinkService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class LinkController {
	private final LinkService linkService;

	@PostMapping("/")
	public ResponseEntity<String> generateLink(@RequestParam("redirectLink") String redirectLink,
			@RequestParam(name = "time", required = false) Double time, @RequestParam(name = "key") String key,
			HttpServletRequest request) {
		return ResponseEntity.ok(linkService.saveOrGetLink(redirectLink, time, key, request));
	}
 
	@GetMapping("/{generated}")
	public ResponseEntity<?> redirectLink(@PathVariable("generated") String generated, HttpServletRequest request) {
		HttpHeaders header = new HttpHeaders();
		header.add("Location", linkService.getLinkByGeneratedValue(generated, request).getLinkRedirect());
		return new ResponseEntity<>(header, HttpStatus.FOUND);
	}

	@GetMapping("/{generated}/stats/{key}")
	public ResponseEntity<String> statsLink(@PathVariable("generated") String generated,
			@PathVariable("key") String key, HttpServletRequest request) {
		return ResponseEntity.ok(linkService.getLinkByGeneratedToStats(generated, key, request));

	}

	@GetMapping("/{generated}/delete/{key}")
	public String deleteLink(@PathVariable("generated") String generated, HttpServletRequest request,
			@PathVariable("key") String key) {
		linkService.deleteLinkByGeneratedValue(generated, key, request);
		return "Short link deleted";
	}

}

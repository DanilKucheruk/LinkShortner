package com.linkshortener.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.TreeMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class Sha1Generator {
	public String generate(String key, HttpServletRequest request) {
		try {
			Map<String, String> headers = new TreeMap<>();
			request.getHeaderNames().asIterator().forEachRemaining((s) -> {
				if (s.equals("user-agent") || s.equals("sec-ch-ua-platform") || s.equals("cookie")) {
					headers.put(s, request.getHeader(s));
				}
			});
			StringBuilder strb = new StringBuilder(headers.toString().replaceAll(", ", "&"));
			strb.append(strb.substring(1, strb.length() - 1));
			if (key != "") {
				strb.append("&" + key);
			}
			String input = strb.toString();
			String sha1 = null;
			MessageDigest sh1Digest = MessageDigest.getInstance("SHA-1");
			byte[] bytes = sh1Digest.digest(input.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : bytes) {
				sb.append(String.format("%02X", b));
			}
			sha1 = sb.toString();
			return sha1;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}

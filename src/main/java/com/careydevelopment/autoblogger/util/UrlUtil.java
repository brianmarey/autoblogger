package com.careydevelopment.autoblogger.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(UrlUtil.class);
	
	public static String getFullHtml(String url) {
		String fullHtml = "";
		
		try {
			URLConnection conn = new URL(url).openConnection();
			conn.addRequestProperty("User-Agent", "Mozilla/4.76");
								    			
			StringBuilder builder = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
	            try (Stream<String> stream = reader.lines()) {
	 				stream.forEach(line -> {
	 					builder.append(line);
	 					builder.append("\n");
		            });	            	
	            }
			}
			
			fullHtml = builder.toString();
		} catch (Exception e) {
			LOGGER.error("Problem reading URL " + url, e);
		}
		
		return fullHtml;
	}
	
	
	public static String getContentBetweenDelimiter(String fullHtml, String startDelimiter, String endDelimiter) {
		String content = "";
		
		if (fullHtml.length() > 1) {
			int start = fullHtml.indexOf(startDelimiter);
			
			if (start > -1) {
				int end = fullHtml.indexOf(endDelimiter, start + startDelimiter.length() + 1);
				
				if (end > -1) {
					content = fullHtml.substring(start + startDelimiter.length(), end);
				}
			}
		}
		
		return content;
	}
	
	
	public static String stripTags(String html) {
		String stripped = html.replaceAll("\\<[^>]*>","");
		return stripped;
	}
}

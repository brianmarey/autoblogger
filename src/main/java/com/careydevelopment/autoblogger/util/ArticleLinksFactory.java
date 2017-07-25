package com.careydevelopment.autoblogger.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.careydevelopment.autoblogger.model.ArticleInfo;

public class ArticleLinksFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArticleLinksFactory.class);
	
	public static List<String> getLinks(ArticleInfo articleInfo) {
		List<String> links = new ArrayList<String>();
		
		try {
			URLConnection conn = new URL(articleInfo.getUrl()).openConnection();
			conn.addRequestProperty("User-Agent", "Mozilla/4.76");
			
			final AtomicInteger count = new AtomicInteger();
			
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
				try (Stream<String> stream = reader.lines()) {
					
					stream.filter(lineCriteria(articleInfo.getTitleDelimiter())).forEach(line ->
					{
						if (count.get() < articleInfo.getArticleCount()) {
							line = line.trim();
							String link = getLink(line,articleInfo.getArticleLinkStart(),articleInfo.getAppendRootUrl(),articleInfo.getUrl());
							links.add(link);
							count.incrementAndGet();
						}
					});
				}
			}
		} catch (Exception e) {
			LOGGER.error("Problem reading URL " + articleInfo.getUrl(), e);
		}
		
		return links;
	}
	
	
	private static String getLink(String line, String articleLinkStart, boolean appendRootUrl, String url) {
		String link = null;
		
		int start = line.indexOf(articleLinkStart);
		if (start > -1) {
			int end = line.indexOf("\"", start + articleLinkStart.length());
			if (end > -1) {
				link = line.substring(start + articleLinkStart.length(), end);

				if (appendRootUrl) {
					link = url + link;
				}
			}
		}
		
		return link;
	}
	
	
	private static Predicate<String> lineCriteria(String delimiter) {
		return line -> line.indexOf(delimiter) > -1; 
	}
}

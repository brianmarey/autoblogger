package com.careydevelopment.autoblogger;

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

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class ArticleFetcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArticleFetcher.class);
	
	private MongoDb db = null;
	
	public static void main(String[] args) {
		ArticleFetcher fetcher = new ArticleFetcher();
		fetcher.go();		
	}
	
	
	private void go() {
		db = MongoDb.getMongoDb();
		
		MongoCollection<Document> sourceCollection = db.getCollection("source");
		FindIterable<Document> docs = sourceCollection.find();
		for (Document d : docs) {
			getArticlesFromSource(d);
		}
	}
	
	
	private void getArticlesFromSource(Document d) {
		String name = d.getString("name");
		String url = d.getString("url");
		double articleCount = d.getDouble("count");
		String topDelimiter = d.getString("topdelimiter");
		double topDelimiterCount = d.getDouble("topdelimitercount");
		String afterContentDelimiter = d.getString("aftercontentdelimiter");
		String tagsDelimiterTop = d.getString("tagsdelimitertop");
		String tagsDelimiterBottom = d.getString("tagsdelimiterbottom");
		String titleDelimiter = d.getString("titledelimiter");
		String articleLinkStart = d.getString("articlelinkstart");
		boolean appendRootUrl = d.getBoolean("appendrooturl");
		
		List<String> links = getLinks(url, articleCount, titleDelimiter, articleLinkStart, appendRootUrl);
		
		links.forEach((link) -> {
			getArticle(link, topDelimiter, topDelimiterCount, afterContentDelimiter, tagsDelimiterTop, tagsDelimiterBottom);
		});
	}
	
	
	private void getArticle(String link, String topDelimiter, double topDelimiterCount, 
		String afterContentDelimiter, String tagsDelimiterTop, String tagsDelimiterBottom) {
		
		
	}
	
	
	private List<String> getLinks(String url, double articleCount, String titleDelimiter, String articleLinkStart, boolean appendRootUrl) {
		List<String> links = new ArrayList<String>();
		
		try {
			URLConnection conn = new URL(url).openConnection();
			conn.addRequestProperty("User-Agent", "Mozilla/4.76");
			
			final AtomicInteger count = new AtomicInteger();
			
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
				try (Stream<String> stream = reader.lines()) {
					
					stream.filter(lineCriteria(titleDelimiter)).forEach(line ->
					{
						if (count.get() < articleCount) {
							line = line.trim();
							String link = getLink(line,articleLinkStart,appendRootUrl,url);
							links.add(link);
							count.incrementAndGet();
						}
					});
				}
			}
		} catch (Exception e) {
			LOGGER.error("Problem reading URL " + url, e);
		}
		
		return links;
	}
	
	
	private String getLink(String line, String articleLinkStart, boolean appendRootUrl, String url) {
		String link = null;
		
		int start = line.indexOf(articleLinkStart);
		if (start > -1) {
			int end = line.indexOf("\"", start);
			if (end > -1) {
				link = line.substring(start, end);

				if (appendRootUrl) {
					link = url + link;
				}
			}
		}
		
		return link;
	}
	
	private Predicate<String> lineCriteria(String delimiter) {
		return line -> line.indexOf(delimiter) > -1; 
	}
}

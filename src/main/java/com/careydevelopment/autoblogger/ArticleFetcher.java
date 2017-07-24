package com.careydevelopment.autoblogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang.StringEscapeUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.careydevelopment.autoblogger.model.ArticleInfo;
import com.careydevelopment.autoblogger.util.StringUtil;
import com.careydevelopment.autoblogger.util.UrlUtil;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

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
		ArticleInfo articleInfo = new ArticleInfo(d);
		
		List<String> links = getLinks(articleInfo);
		
		//links.forEach((link) -> {
			getArticle(links.get(0), articleInfo);
		//});
	}
	
	
	private void getArticle(String link, ArticleInfo articleInfo) {
		MongoCollection<Document> articleCollection = db.getCollection("article");
		Document exists = articleCollection.find(Filters.eq("url", link)).first();
		
		if (exists == null) {
			persistArticle(link, articleInfo);
		}
	}
	
	
	private void persistArticle(String link, ArticleInfo articleInfo) {
		String text = getTextFromLink(link, articleInfo);
		String blurb = getBlurb(text);
	}	
	
	
	private String getBlurb(String text) {
		String blurb = "";
		String[] parts = text.split("\n");
		
		Stream<String> stream = Arrays.stream(parts);
		StringBuilder blurbBuilder = new StringBuilder();
		
		stream.filter(line -> line.trim().length() > 1).forEach(line -> {
			if (StringUtil.countNumberOfWords(blurbBuilder.toString()) < 120) {
				blurbBuilder.append(line);
				blurbBuilder.append("\n");
			}
        });
				
		blurb = blurbBuilder.toString();
		
		return blurb;
	}
	
	
	private String getTextFromLink(String url, ArticleInfo articleInfo) {
		String fullHtml = UrlUtil.getFullHtml(url);
		String text = getRawText(fullHtml, articleInfo);
		
		return text;
	}

	
	private String getRawText(String fullHtml, ArticleInfo articleInfo) {
		String content = UrlUtil.getContentBetweenDelimiter(fullHtml, articleInfo.getTopDelimiter(), articleInfo.getAfterContentDelimiter());
		
		content = UrlUtil.stripTags(content);
		
		String text = StringEscapeUtils.unescapeHtml(content);

		return text;
	}

	
	private List<String> getLinks(ArticleInfo articleInfo) {
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

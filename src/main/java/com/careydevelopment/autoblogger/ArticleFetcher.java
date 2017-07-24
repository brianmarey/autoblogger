package com.careydevelopment.autoblogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang.StringEscapeUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.careydevelopment.autoblogger.model.ArticleInfo;
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
		String blurb = getBlurb(text, articleInfo);
	}	
	
	
	private String getBlurb(String text, ArticleInfo articleInfo) {
		int start = -1;

		start = text.indexOf(articleInfo.getTopDelimiter(), start + 1);
		
		if (start > -1) {
			int end = text.indexOf(articleInfo.getAfterContentDelimiter(), start + 1);
			if (end > -1) {
				String rawContent = text.substring(start + articleInfo.getTopDelimiter().length(), end);
				rawContent = rawContent.trim();
				try {
					try (BufferedReader reader = new BufferedReader(new StringReader(rawContent))) {
						try (Stream<String> stream = reader.lines()) {
							stream.forEach(line -> {
								System.err.println(line);
							});
						}
					}
				} catch (Exception e) {
					LOGGER.error("Problem getting blurb from text " + text.substring(0, 40), e);
				}
			}
		}
		
		return "";
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

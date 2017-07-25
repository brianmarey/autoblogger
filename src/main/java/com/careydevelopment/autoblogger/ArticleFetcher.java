package com.careydevelopment.autoblogger;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.careydevelopment.autoblogger.db.mongo.MongoDb;
import com.careydevelopment.autoblogger.model.Article;
import com.careydevelopment.autoblogger.model.ArticleInfo;
import com.careydevelopment.autoblogger.util.ArticleFactory;
import com.careydevelopment.autoblogger.util.ArticleLinksFactory;
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
		
		List<String> links = ArticleLinksFactory.getLinks(articleInfo);
		
		//links.forEach((link) -> {
			getArticle(links.get(0), articleInfo);
		//});
	}
	
	
	private void getArticle(String link, ArticleInfo articleInfo) {
		MongoCollection<Document> articleCollection = db.getCollection("article");
		Document exists = articleCollection.find(Filters.eq("link", link)).first();
		
		if (exists == null) {
			persistArticle(link, articleInfo);
		}
	}
	
	
	private void persistArticle(String link, ArticleInfo articleInfo) {
		LOGGER.debug("Url " + link + " doesn't exist, persisting...");

		Article article = ArticleFactory.createArticleFromLink(link, articleInfo);
		Document document = createDocumentFromArticle(article);
		
		db.insertDocument(document, "article");
	}	
	
	
	private Document createDocumentFromArticle(Article article) {
		Document document = new Document("articleDate",article.getArticleDate());
		document.append("category", "");
		document.append("blogName", article.getBlogName());
		document.append("blurb", article.getBlurb());
		document.append("link", article.getLink());
		document.append("tags", article.getTags());
		document.append("title", article.getTitle());
		document.append("active", false);
		
		return document;
	}
}

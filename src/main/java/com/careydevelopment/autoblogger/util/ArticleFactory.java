package com.careydevelopment.autoblogger.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.careydevelopment.autoblogger.model.Article;
import com.careydevelopment.autoblogger.model.ArticleInfo;

public class ArticleFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArticleFactory.class);
	
	public static Article createArticleFromLink(String link, ArticleInfo articleInfo) {
		Article article = new Article();
		article.setArticleDate(new Date());
		article.setLink(link);
		article.setBlogName(articleInfo.getName());
		
		setTitleTagsAndText(link, articleInfo, article);
		setBlurb(article);
		
		return article;
	}
	
	
	private static List<String> getTags(String fullHtml, ArticleInfo articleInfo) {
		String tagSection = UrlUtil.getContentBetweenDelimiter(fullHtml, articleInfo.getTagsDelimiterTop(), articleInfo.getTagsDelimiterBottom());
		List<String> tags = new ArrayList<String>();
		
		if (tagSection.length() > 1) {
			tagSection = UrlUtil.stripTags(tagSection).trim();

			if (tagSection.indexOf(",") > -1) {
				tags = getCommaDelimitedTags(tagSection);
			} else {
				tags = getCrDelimitedTags(tagSection);
			}	
		}
						
		return tags;
	}

	
	private static List<String> getCommaDelimitedTags(String tagSection) {
		List<String> tags = new ArrayList<String>();

		String[] parts = tagSection.split(",");
		Stream<String> stream = Arrays.stream(parts);
		
		stream.forEach(line -> {
			line = line.trim();
			if (line.length() > 1) {
				tags.add(line);
				LOGGER.debug("adding tag " + line);				
			}
        });
				
		return tags;
	}
	
	
	private static List<String> getCrDelimitedTags(String tagSection) {
		List<String> tags = new ArrayList<String>();
		
		String[] parts = tagSection.split("\n");	
		Stream<String> stream = Arrays.stream(parts);
		
		stream.forEach(line -> {
			line = line.trim();
			if (line.length() > 1) {
				tags.add(line);
				LOGGER.debug("adding tag " + line);				
			}
        });
		
		return tags;
	}
	
	
	private static void setBlurb(Article article) {
		String blurb = "";
		String[] parts = article.getBody().split("\n");
		
		Stream<String> stream = Arrays.stream(parts);
		StringBuilder blurbBuilder = new StringBuilder();
		
		stream.filter(line -> line.trim().length() > 1).forEach(line -> {
			if (StringUtil.countNumberOfWords(blurbBuilder.toString()) < 120) {
				blurbBuilder.append(line);
				blurbBuilder.append("\n");
			}
        });
				
		blurb = blurbBuilder.toString().trim();
		
		LOGGER.debug("blurb is " + blurb);
		
		article.setBlurb(blurb);
	}
	
	
	private static void setTitleTagsAndText(String url, ArticleInfo articleInfo, Article article) {
		String fullHtml = UrlUtil.getFullHtml(url);
		String text = getRawText(fullHtml, articleInfo);
		String title = getTitle(fullHtml, articleInfo);
		List<String> tags = getTags(fullHtml, articleInfo);
		
		LOGGER.debug("title is " + title);
		
		article.setBody(text);
		article.setTitle(title);
		article.setTags(tags);
	}

	
	private static String getTitle(String fullHtml, ArticleInfo articleInfo) {
		String title = UrlUtil.getContentBetweenDelimiter(fullHtml, articleInfo.getArticleTitleStartDelimiter(), articleInfo.getArticleTitleEndDelimiter());
		title = UrlUtil.stripTags(title).trim();
		title = StringEscapeUtils.unescapeHtml(title);
		return title;
	}
	
	
	private static String getRawText(String fullHtml, ArticleInfo articleInfo) {
		String content = UrlUtil.getContentBetweenDelimiter(fullHtml, articleInfo.getTopDelimiter(), articleInfo.getAfterContentDelimiter());
		content = UrlUtil.stripTags(content);
		String text = StringEscapeUtils.unescapeHtml(content);
		return text;
	}
}

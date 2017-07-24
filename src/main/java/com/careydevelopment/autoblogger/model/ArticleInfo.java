package com.careydevelopment.autoblogger.model;

import org.bson.Document;

public class ArticleInfo {
	
	private String name;
	private String url;
	private Double articleCount;
	private String topDelimiter;
	private Double topDelimiterCount;
	private String afterContentDelimiter;
	private String tagsDelimiterTop;
	private String tagsDelimiterBottom;
	private String titleDelimiter;
	private String articleLinkStart;
	private Boolean appendRootUrl;
	
	
	public ArticleInfo(Document d) {
		name = d.getString("name");
		url = d.getString("url");
		articleCount = d.getDouble("count");
		topDelimiter = d.getString("topdelimiter");
		topDelimiterCount = d.getDouble("topdelimitercount");
		afterContentDelimiter = d.getString("aftercontentdelimiter");
		tagsDelimiterTop = d.getString("tagsdelimitertop");
		tagsDelimiterBottom = d.getString("tagsdelimiterbottom");
		titleDelimiter = d.getString("titledelimiter");
		articleLinkStart = d.getString("articlelinkstart");
		appendRootUrl = d.getBoolean("appendrooturl");
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Double getArticleCount() {
		return articleCount;
	}
	public void setArticleCount(Double articleCount) {
		this.articleCount = articleCount;
	}
	public String getTopDelimiter() {
		return topDelimiter;
	}
	public void setTopDelimiter(String topDelimiter) {
		this.topDelimiter = topDelimiter;
	}
	public Double getTopDelimiterCount() {
		return topDelimiterCount;
	}
	public void setTopDelimiterCount(Double topDelimiterCount) {
		this.topDelimiterCount = topDelimiterCount;
	}
	public String getAfterContentDelimiter() {
		return afterContentDelimiter;
	}
	public void setAfterContentDelimiter(String afterContentDelimiter) {
		this.afterContentDelimiter = afterContentDelimiter;
	}
	public String getTagsDelimiterTop() {
		return tagsDelimiterTop;
	}
	public void setTagsDelimiterTop(String tagsDelimiterTop) {
		this.tagsDelimiterTop = tagsDelimiterTop;
	}
	public String getTagsDelimiterBottom() {
		return tagsDelimiterBottom;
	}
	public void setTagsDelimiterBottom(String tagsDelimiterBottom) {
		this.tagsDelimiterBottom = tagsDelimiterBottom;
	}
	public String getTitleDelimiter() {
		return titleDelimiter;
	}
	public void setTitleDelimiter(String titleDelimiter) {
		this.titleDelimiter = titleDelimiter;
	}
	public String getArticleLinkStart() {
		return articleLinkStart;
	}
	public void setArticleLinkStart(String articleLinkStart) {
		this.articleLinkStart = articleLinkStart;
	}
	public Boolean getAppendRootUrl() {
		return appendRootUrl;
	}
	public void setAppendRootUrl(Boolean appendRootUrl) {
		this.appendRootUrl = appendRootUrl;
	}
	
	

}

package com.careydevelopment.autoblogger.model;

import java.util.Date;
import java.util.List;

public class Article {

	private String title;
	private String blurb;
	private Date articleDate;
	private String body;
	private String link;
	private List<String> tags;
	private String blogName;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBlurb() {
		return blurb;
	}
	public void setBlurb(String blurb) {
		this.blurb = blurb;
	}
	public Date getArticleDate() {
		return articleDate;
	}
	public void setArticleDate(Date articleDate) {
		this.articleDate = articleDate;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public String getBlogName() {
		return blogName;
	}
	public void setBlogName(String blogName) {
		this.blogName = blogName;
	}
	
	
}

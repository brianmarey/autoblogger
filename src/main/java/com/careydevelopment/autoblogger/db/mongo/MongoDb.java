package com.careydevelopment.autoblogger;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDb {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDb.class);
	
	private MongoDb db = null;
	private MongoClient client = null;
	private MongoDatabase database = null;
	
	private MongoDb() {}
	
	
	public MongoDb getMongoDb() {
		if (db == null) {
			db = new MongoDb();
		}
		
		return db;
	}
	
	
	public MongoClient setupDb() {
		MongoClientURI connectionString = new MongoClientURI("xxxxxxxxx");
		client = new MongoClient(connectionString);
		database = client.getDatabase("brianmcarey");
		
		return client;
	}
	
	
	public MongoCollection<Document> getCollection(String collectionName) {
		MongoCollection<Document> collection = database.getCollection(collectionName);
		return collection;
	}
}

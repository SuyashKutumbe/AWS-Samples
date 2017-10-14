package com.example.dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CRUDOnMoviesTable {

	// static DynamoDB dynamoDB = new
	// DynamoDB(AmazonDynamoDBClientBuilder.standard().withCredentials(new
	// ProfileCredentialsProvider()).build());
	static String tableName = "Movies";
	// static Table table = dynamoDB.getTable(tableName);

	static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
			.withRegion(Regions.US_WEST_2)
			.withCredentials(new ProfileCredentialsProvider()).build();
	
	public static void main(String[] args) {

		for(int i=0;i<100;i++) {
			createItems();
		getItem();
		//deleteItem();
		}
	}

	private static void getItem() {
		
		ScanRequest scanRequest = new ScanRequest().withTableName(tableName);

		ScanResult result = amazonDynamoDB.scan(scanRequest);
		for (Map<String, AttributeValue> item : result.getItems()) {
			System.out.println(item);
		}
	}

	private static void createItems() {
		Map<String, AttributeValue> map = new HashMap<>();
		map.put("year", new AttributeValue().withN(new Random().nextInt(2000) + ""));
		map.put("title", new AttributeValue().withS("Hello Dynamo 2019!"));
		PutItemRequest request = new PutItemRequest().withTableName(tableName).withItem(map);
		amazonDynamoDB.putItem(request);
	}
	
	private static void deleteItem() {
		Map<String, AttributeValue> map = new HashMap<>();
		map.put("year", new AttributeValue().withN("2017"));
		map.put("title", new AttributeValue().withS("Hello Dynamo 2017!"));
		DeleteItemRequest request = new DeleteItemRequest().withTableName(tableName).withKey(map);
		amazonDynamoDB.deleteItem(request);
	}
	
	

}

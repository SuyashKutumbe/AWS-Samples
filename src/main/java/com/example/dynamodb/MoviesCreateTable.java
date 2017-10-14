package com.example.dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

public class MoviesCreateTable {

//	static DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withCredentials(new ProfileCredentialsProvider()).build());
	public static void main(String[] args) throws Exception {

		AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
				.withCredentials(new ProfileCredentialsProvider()).build();
	    String tableName = "Movies";

	    try {
	    	AttributeDefinition definition1 = new AttributeDefinition()
	    			.withAttributeName("year").withAttributeType(ScalarAttributeType.N);
	    	
	    	AttributeDefinition definition2 = new AttributeDefinition()
	    			.withAttributeName("title").withAttributeType(ScalarAttributeType.S);
	    	
	    	KeySchemaElement element1 = new KeySchemaElement()
	    			.withAttributeName("year").withKeyType(KeyType.HASH);
	    	
	    	KeySchemaElement element2 = new KeySchemaElement()
	    			.withAttributeName("title").withKeyType(KeyType.RANGE);
	    	
	    	ProvisionedThroughput throughput = new ProvisionedThroughput()
	    			.withReadCapacityUnits(10L)
	    			.withWriteCapacityUnits(10L);
	    	
	    	CreateTableRequest createTableRequest = new CreateTableRequest()
	    			.withTableName(tableName)
	    			.withAttributeDefinitions(definition1)
	    			.withKeySchema(element1)
	    			.withAttributeDefinitions(definition2)
	    			.withKeySchema(element2)
	    			.withProvisionedThroughput(throughput);
	    	
	    	CreateTableResult result = amazonDynamoDB.createTable(createTableRequest);
	        System.out.println("Attempting to create table; please wait...");
	        /*Table table = dynamoDB.createTable(tableName,
	                           Arrays.asList(
	                               new KeySchemaElement("year", KeyType.HASH), //Partition key
	                               new KeySchemaElement("title", KeyType.RANGE)), //Sort key
	                           Arrays.asList(
	                               new AttributeDefinition("year", ScalarAttributeType.N),
	                               new AttributeDefinition("title", ScalarAttributeType.S)),
	                           new ProvisionedThroughput(10L, 10L));
	        table.waitForActive();*/
	        System.out.println("Success.  Table status: " + result.getTableDescription().getTableStatus());

	    } catch (Exception e) {
	        System.err.println("Unable to create table: ");
	        System.err.println(e.getMessage());
	    }

	}
}

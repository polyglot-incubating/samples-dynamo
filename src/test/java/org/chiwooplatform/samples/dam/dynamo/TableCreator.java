package org.chiwooplatform.samples.dam.dynamo;

import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

public class TableCreator {

	public static boolean createTableIfNotExists(AmazonDynamoDB dynamoDB,
			DynamoDBMapper mapper, Class<?> entity, String tableName) {
		final DeleteTableRequest deleteTableRequest = new DeleteTableRequest(tableName);
		TableUtils.deleteTableIfExists(dynamoDB, deleteTableRequest);
		CreateTableRequest createTableRequest = mapper.generateCreateTableRequest(entity);
		createTableRequest.setProvisionedThroughput(new ProvisionedThroughput()
				.withReadCapacityUnits(5L).withWriteCapacityUnits(5L));
		return TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
	}

	public static boolean createTableIfNotExists(AmazonDynamoDB dynamoDB,
			DynamoDBMapper mapper, Class<?> entity, String tableName,
			String... indexNames) {

		final DeleteTableRequest deleteTableRequest = new DeleteTableRequest(tableName);
		TableUtils.deleteTableIfExists(dynamoDB, deleteTableRequest);
		CreateTableRequest createTableRequest = mapper.generateCreateTableRequest(entity);
		createTableRequest.setProvisionedThroughput(new ProvisionedThroughput()
				.withReadCapacityUnits(5L).withWriteCapacityUnits(5L));
		List<GlobalSecondaryIndex> gsi = createTableRequest.getGlobalSecondaryIndexes();
		if (gsi != null) {
			final ProvisionedThroughput idxThroughput = new ProvisionedThroughput()
					.withReadCapacityUnits(5L).withWriteCapacityUnits(5L);
			for (GlobalSecondaryIndex gs : gsi) {
				final String indexName = gs.getIndexName();
				for (String idxName : indexNames) {
					if (idxName.equals(indexName)) {
						gs.setProvisionedThroughput(idxThroughput);
					}
				}
			}
		}
		createTableRequest.getGlobalSecondaryIndexes().get(0)
				.setProvisionedThroughput(new ProvisionedThroughput()
						.withReadCapacityUnits(5L).withWriteCapacityUnits(5L));
		return TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
	}

	public static boolean deleteTableIfExists(AmazonDynamoDB dynamoDB, String tableName) {
		final DeleteTableRequest deleteTableRequest = new DeleteTableRequest(tableName);
		return TableUtils.deleteTableIfExists(dynamoDB, deleteTableRequest);
	}

}
